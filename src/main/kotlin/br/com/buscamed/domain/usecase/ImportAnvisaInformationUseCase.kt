package br.com.buscamed.domain.usecase

import br.com.buscamed.core.utils.HashUtils
import br.com.buscamed.domain.model.system.SystemProcessControl
import br.com.buscamed.domain.parser.AnvisaCsvParser
import br.com.buscamed.domain.repository.AnvisaMedicationRepository
import br.com.buscamed.domain.repository.SystemProcessControlRepository
import br.com.buscamed.domain.service.AnvisaIntegrationService
import br.com.buscamed.domain.service.CsvStorageService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import java.time.Instant

class ImportAnvisaInformationUseCase(
    private val integrationService: AnvisaIntegrationService,
    private val csvParser: AnvisaCsvParser,
    private val medicationRepository: AnvisaMedicationRepository,
    private val storageService: CsvStorageService,
    private val processControlRepository: SystemProcessControlRepository
) {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private val processControlId = "ANVISA_MEDICATIONS_SYNC"

    suspend operator fun invoke() = withContext(Dispatchers.IO) {
        logger.info("Iniciando rotina de sincronização de medicamentos da ANVISA.")
        val rawCsvBytes = integrationService.downloadPricesCsv()

        val parseResult = csvParser.parse(rawCsvBytes)

        if (parseResult.medications.isEmpty()) {
            logger.warn("O arquivo CSV da ANVISA foi processado, mas não retornou medicamentos válidos.")
            return@withContext
        }

        val currentFileHash = HashUtils.generateSha256(parseResult.cleanedCsvBytes)
        val lastControlStatus = processControlRepository.findById(processControlId)

        if (lastControlStatus?.lastHash == currentFileHash) {
            logger.info("O arquivo fornecido pela ANVISA é idêntico ao último processado. A sincronização será ignorada.")
            return@withContext
        }

        logger.info("Diferença detectada. Atualizando base de dados com ${parseResult.medications.size} registros via Upsert.")
        medicationRepository.saveAll(parseResult.medications)

        val storagePath = storageService.upload(parseResult.cleanedCsvBytes, "text/csv")

        val newControlStatus = SystemProcessControl(
            id = processControlId,
            lastHash = currentFileHash,
            lastStorageFilePath = storagePath,
            lastUpdated = Instant.now()
        )

        processControlRepository.save(newControlStatus)
        logger.info("Sincronização finalizada com sucesso.")
    }
}