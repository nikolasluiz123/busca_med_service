package br.com.buscamed.domain.usecase

import br.com.buscamed.domain.model.anvisa.AnvisaMedicationLeaflet
import br.com.buscamed.domain.repository.AnvisaMedicationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory

/**
 * Caso de uso orquestrador para realizar o processamento e o resumo de bulas (pacientes e profissionais)
 * de forma sequencial, consolidando a persistência em um único lote.
 *
 * @property repository Repositório de medicamentos da ANVISA para salvar os dados em lote.
 * @property resumePatientLeafletUseCase Caso de uso para resumo de bulas de pacientes.
 * @property resumeProfessionalLeafletUseCase Caso de uso para resumo de bulas de profissionais.
 */
class ResumeLeafletUseCase(
    private val repository: AnvisaMedicationRepository,
    private val resumePatientLeafletUseCase: ResumePatientLeafletUseCase,
    private val resumeProfessionalLeafletUseCase: ResumeProfessionalLeafletUseCase
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    /**
     * Coordena o processamento sequencial baseando-se em uma lista de medicamentos pendentes.
     * Falhas individuais em um processamento não impedem a execução dos demais.
     *
     * @param limit A quantidade máxima de medicamentos a serem processados nesta execução.
     */
    suspend operator fun invoke(limit: Int = 5) = withContext(Dispatchers.IO) {
        logger.info("Iniciando verificação de bulas pendentes de resumo.")
        
        val patientPending = repository.findMedicationsWithoutPatientLeafletsResume(limit)
        val professionalPending = repository.findMedicationsWithoutProfessionalLeafletsResume(limit)

        val pendingMedicationIds = (patientPending.map { it.ggremCode } + professionalPending.map { it.ggremCode })
            .distinct()
            .take(limit)

        if (pendingMedicationIds.isEmpty()) {
            logger.info("Nenhuma bula pendente de resumo foi encontrada. Finalizando execução.")
            return@withContext
        }

        logger.info("Encontradas ${pendingMedicationIds.size} bulas para processamento. Iniciando resumo sequencial...")

        val leafletsToSave = mutableListOf<AnvisaMedicationLeaflet>()
        val processedMedicationIds = mutableSetOf<String>()

        for (medicationId in pendingMedicationIds) {
            val patientResult = runCatching {
                resumePatientLeafletUseCase(medicationId = medicationId, autoSave = false)
            }.onFailure { error ->
                logger.error("Erro ao resumir bula de paciente para medicamento $medicationId", error)
            }.getOrNull()

            val professionalResult = runCatching {
                resumeProfessionalLeafletUseCase(medicationId = medicationId, autoSave = false)
            }.onFailure { error ->
                logger.error("Erro ao resumir bula de profissional para medicamento $medicationId", error)
            }.getOrNull()

            if (patientResult != null) {
                leafletsToSave.add(patientResult)
                processedMedicationIds.add(medicationId)
            }

            if (professionalResult != null) {
                leafletsToSave.add(professionalResult)
                processedMedicationIds.add(medicationId)
            }
        }

        if (leafletsToSave.isNotEmpty()) {
            repository.saveLeaflets(
                processedMedicationIds.toList(),
                leafletsToSave,
                null
            )
            logger.info("Finalizado o processamento de ${processedMedicationIds.size} bulas pendentes de resumo, com lote persistido com sucesso.")
        } else {
            logger.info("Nenhuma das bulas pendentes pôde ser processada (verifique erros individuais ou falta de conteúdo de PDF).")
        }
    }
}
