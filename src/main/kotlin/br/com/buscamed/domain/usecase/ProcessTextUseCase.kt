package br.com.buscamed.domain.usecase

import br.com.buscamed.domain.exceptions.BusinessException
import br.com.buscamed.domain.model.LLMExecutionHistory
import br.com.buscamed.domain.model.enumeration.ExecutionType
import br.com.buscamed.domain.repository.LLMExecutionHistoryRepository
import br.com.buscamed.domain.service.ImageStorageService
import br.com.buscamed.domain.service.LLMTextProcessService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant

/**
 * Caso de uso universal para extração de dados estruturados a partir de texto base utilizando LLMs.
 *
 * Este caso de uso invoca o serviço de processamento de texto e registra o histórico
 * da execução no banco de dados, incluindo métricas de tokens e o status de sucesso.
 *
 * @property executionHistoryRepository O repositório para salvar o histórico da execução.
 * @property llmProcessService O serviço responsável pela comunicação com o modelo de linguagem.
 */
class ProcessTextUseCase(
    private val executionHistoryRepository: LLMExecutionHistoryRepository,
    private val llmProcessService: LLMTextProcessService,
    private val storageService: ImageStorageService
) {
    private val uploadImageScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /**
     * Executa o processo de extração de dados do texto.
     *
     * @param text O texto bruto extraído pelo client a ser processado pela LLM.
     * @param mimeType O tipo MIME da imagem.
     * @return O resultado processado pela LLM em formato de string JSON.
     * @throws Exception Se ocorrer algum erro durante a comunicação com o serviço da LLM.
     */
    suspend operator fun invoke(
        text: String?,
        imageBytes: ByteArray?,
        mimeType: String?,
        clientProcessorVersion: String?
    ): String = withContext(Dispatchers.IO) {
        if (text.isNullOrBlank()) {
            throw BusinessException("É obrigatório informar o texto para processamento.")
        }

        if (imageBytes == null) {
            throw BusinessException("É obrigatório informar a imagem que originou o texto.")
        }

        if (mimeType.isNullOrBlank()) {
            throw BusinessException("É obrigatório informar o tipo da imagem.")
        }

        if (clientProcessorVersion.isNullOrEmpty()) {
            throw BusinessException("É obrigatório informar a versão do pipeline de processamento do client.")
        }

        var executionSuccess = true
        val executionStart = Instant.now()
        var inputTokens = 0
        var outputTokens = 0
        var resultText = "{}"
        var prompt = ""
        var llmModel = ""

        try {
            val llmResult = llmProcessService.process(text)
            inputTokens = llmResult.inputTokens
            outputTokens = llmResult.outputTokens
            resultText = llmResult.resultText
            prompt = llmResult.promptName
            llmModel = llmResult.llmModel

            resultText
        } catch (e: Exception) {
            executionSuccess = false
            throw e
        } finally {
            val history = LLMExecutionHistory(
                type = ExecutionType.TEXT,
                inputText = text,
                inputTokens = inputTokens,
                outputTokens = outputTokens,
                result = resultText,
                success = executionSuccess,
                startDate = executionStart,
                endDate = Instant.now(),
                prompt = prompt,
                clientProcessorVersion = clientProcessorVersion,
                llmModel = llmModel
            )

            val historyId = executionHistoryRepository.save(history)

            uploadImageScope.launch {
                val path = storageService.upload(imageBytes, mimeType)
                executionHistoryRepository.updateImageStoragePath(historyId, path)
            }
        }
    }
}
