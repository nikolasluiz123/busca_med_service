package br.com.buscamed.domain.usecase

import br.com.buscamed.domain.model.LLMExecutionHistory
import br.com.buscamed.domain.model.anvisa.AnvisaMedicationLeaflet
import br.com.buscamed.domain.model.anvisa.enumeration.AnvisaMedicationLeafletIdentifier
import br.com.buscamed.domain.model.enumeration.ExecutionType
import br.com.buscamed.domain.repository.AnvisaMedicationRepository
import br.com.buscamed.domain.repository.LLMExecutionHistoryRepository
import br.com.buscamed.domain.service.LLMPDFProcessService
import br.com.buscamed.domain.service.LeafletStorageService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import java.time.Instant

/**
 * Caso de uso responsável por recuperar, processar e resumir a bula destinada a pacientes de um medicamento específico.
 *
 * @property repository Repositório de medicamentos da ANVISA para busca e persistência.
 * @property storageService Serviço de armazenamento para download do documento em PDF.
 * @property pdfProcessService Serviço de processamento na LLM especializado em bulas de paciente.
 * @property executionHistoryRepository Repositório de histórico de execução da LLM.
 */
class ResumePatientLeafletUseCase(
    private val repository: AnvisaMedicationRepository,
    private val storageService: LeafletStorageService,
    private val pdfProcessService: LLMPDFProcessService,
    private val executionHistoryRepository: LLMExecutionHistoryRepository
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    /**
     * Executa o processamento da bula de paciente para o medicamento informado.
     *
     * @param medicationId Identificador do medicamento (GGREM).
     * @param autoSave Define se o caso de uso deve persistir a alteração automaticamente no repositório.
     * @return A bula com o resumo preenchido, ou null caso não seja possível processar.
     */
    suspend operator fun invoke(medicationId: String, autoSave: Boolean = false): AnvisaMedicationLeaflet? = withContext(Dispatchers.IO) {
        val leaflet = repository.findLeafletBy(medicationId, AnvisaMedicationLeafletIdentifier.PATIENT)

        if (leaflet == null || leaflet.leafletStoragePath == null || leaflet.leafletResume != null) {
            return@withContext null
        }
        
        logger.info("Iniciando extração e processamento da bula de paciente para o medicamento GGREM [{}].", medicationId)

        val fileName = leaflet.leafletStoragePath.substringAfter("gs://").substringAfter("/")
        val pdfBytes = storageService.download(fileName)
        
        var executionSuccess = true
        val executionStart = Instant.now()
        var inputTokens = 0
        var outputTokens = 0
        var resultText = "{}"
        var prompt = ""
        var llmModel = ""

        try {
            val result = pdfProcessService.process(pdfBytes)
            inputTokens = result.inputTokens
            outputTokens = result.outputTokens
            resultText = result.resultText
            prompt = result.promptName
            llmModel = result.llmModel

            val now = Instant.now()
            val updatedLeaflet = leaflet.copy(
                leafletResume = resultText,
                leafletResumeCreatedAt = leaflet.leafletResumeCreatedAt ?: now,
                leafletResumeUpdatedAt = now
            )

            if (autoSave) {
                repository.saveLeaflets(
                    medicationIds = listOf(medicationId),
                    leaflets = listOf(updatedLeaflet),
                    leafletIdentifier = AnvisaMedicationLeafletIdentifier.PATIENT
                )
            }
            
            logger.info("Processamento da bula de paciente [{}] concluído com sucesso.", medicationId)
            updatedLeaflet
        } catch (e: Exception) {
            executionSuccess = false
            logger.error("Erro durante o processamento da bula de paciente [{}]. Falha repassada ao histórico.", medicationId)
            throw e
        } finally {
            val history = LLMExecutionHistory(
                type = ExecutionType.PDF,
                inputText = "",
                inputTokens = inputTokens,
                outputTokens = outputTokens,
                result = resultText,
                success = executionSuccess,
                startDate = executionStart,
                endDate = Instant.now(),
                prompt = prompt,
                clientProcessorVersion = "",
                llmModel = llmModel,
                storagePath = leaflet.leafletStoragePath
            )

            executionHistoryRepository.save(history)
        }
    }
}
