package br.com.buscamed.domain.usecase

import br.com.buscamed.domain.model.anvisa.AnvisaMedicationLeaflet
import br.com.buscamed.domain.model.anvisa.enumeration.AnvisaMedicationLeafletIdentifier
import br.com.buscamed.domain.model.system.SystemProcessControl
import br.com.buscamed.domain.repository.AnvisaMedicationRepository
import br.com.buscamed.domain.repository.SystemProcessControlRepository
import br.com.buscamed.domain.service.AnvisaIntegrationService
import br.com.buscamed.domain.service.LeafletStorageService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import java.time.Instant
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

/**
 * Caso de uso para importação e sincronização de bulas em PDF da ANVISA.
 */
class ImportAnvisaLeafletsUseCase(
    private val repository: AnvisaMedicationRepository,
    private val anvisaService: AnvisaIntegrationService,
    private val storageService: LeafletStorageService,
    private val processControlRepository: SystemProcessControlRepository
) {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private val processId = "ANVISA_LEAFLET_IMPORT"

    suspend operator fun invoke() = withContext(Dispatchers.IO) {
        val medications = repository.findPendingLeaflets(50)

        logger.info("${medications.size} medicamentos retornados e serão processados.")

        val delays = listOf(2.seconds, 3.seconds, 4.seconds)

        for (med in medications) {
            try {
                val leaflets = mutableListOf<AnvisaMedicationLeaflet>()
                val ids = anvisaService.fetchLeafletIds(med.registerNumber)

                ids?.patientLeafletId?.let { id ->
                    val bytes = anvisaService.downloadLeafletPdf(id)
                    val storagePath = storageService.upload(med.ggremCode, "patient_$id", bytes)

                    leaflets.add(
                        AnvisaMedicationLeaflet(
                            id = AnvisaMedicationLeafletIdentifier.PATIENT,
                            leafletStoragePath = storagePath,
                        )
                    )

                    delay(delays.random())
                }

                ids?.professionalLeafletId?.let { id ->
                    val bytes = anvisaService.downloadLeafletPdf(id)
                    val storagePath = storageService.upload(med.ggremCode, "professional_$id", bytes)

                    leaflets.add(
                        AnvisaMedicationLeaflet(
                            id = AnvisaMedicationLeafletIdentifier.PROFESSIONAL,
                            leafletStoragePath = storagePath,
                        )
                    )

                    delay(delays.random())
                }

                val hasLeaflet = ids?.patientLeafletId != null || ids?.professionalLeafletId != null

                repository.updateLeafletStatus(med.ggremCode, hasLeaflet, leaflets)
                delay(delays.random())
            } catch (e: Exception) {
                logger.error("Falha ao processar bula para o medicamento ${med.ggremCode}: ${e.message}", e)
            }
        }

        processControlRepository.save(
            control = SystemProcessControl(
                id = processId,
                lastHash = "",
                lastStorageFilePath = "",
                lastUpdated = Instant.now()
            )
        )
    }
}