package br.com.buscamed.domain.usecase

import br.com.buscamed.core.utils.HashUtils
import br.com.buscamed.data.client.anvisa.AnvisaIntegrationClient
import br.com.buscamed.data.client.storage.google.csv.AnvisaCsvGoogleStorageClient
import br.com.buscamed.domain.model.system.SystemProcessControl
import br.com.buscamed.domain.parser.AnvisaCsvParser
import br.com.buscamed.domain.repository.AnvisaMedicationRepository
import br.com.buscamed.domain.repository.SystemProcessControlRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import java.time.Instant

/**
 * Caso de uso responsável por orquestrar a importação e atualização de dados de medicamentos da ANVISA.
 */
class ImportAnvisaInformationUseCase(
    private val integrationClient: AnvisaIntegrationClient,
    private val csvParser: AnvisaCsvParser,
    private val medicationRepository: AnvisaMedicationRepository,
    private val storageClient: AnvisaCsvGoogleStorageClient,
    private val processControlRepository: SystemProcessControlRepository
) {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private val processControlId = "ANVISA_MEDICATIONS_SYNC"

    suspend operator fun invoke() = withContext(Dispatchers.IO) {
        logger.info("Iniciando rotina de sincronização de medicamentos da ANVISA.")
        val rawCsvBytes = integrationClient.downloadPricesCsv()

        val parseResult = csvParser.parse(rawCsvBytes)

        if (parseResult.medications.isEmpty()) {
            logger.warn("O arquivo CSV da ANVISA foi processado, mas não retornou medicamentos válidos.")
            return@withContext
        }

        val currentFileHash = HashUtils.generateSha256(parseResult.cleanedCsvBytes)
        val lastControlStatus = processControlRepository.findById(processControlId)

        if (lastControlStatus?.lastHash == currentFileHash) {
            logger.info("O arquivo fornecido pela ANVISA é idêntico ao último processado (Hash: $currentFileHash). A sincronização será ignorada para poupar recursos.")
            return@withContext
        }

        logger.info("Diferença detectada. Atualizando base de dados com ${parseResult.medications.size} registros via Upsert.")
        medicationRepository.saveAll(parseResult.medications)

        val storagePath = storageClient.upload(parseResult.cleanedCsvBytes, "text/csv")

        val newControlStatus = SystemProcessControl(
            id = processControlId,
            lastHash = currentFileHash,
            lastStorageFilePath = storagePath,
            lastUpdated = Instant.now()
        )

        processControlRepository.save(newControlStatus)
        logger.info("Sincronização finalizada com sucesso. Novo controle de processamento atualizado.")
    }
}