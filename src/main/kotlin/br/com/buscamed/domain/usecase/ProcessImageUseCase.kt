package br.com.buscamed.domain.usecase

import br.com.buscamed.domain.exceptions.BusinessException
import br.com.buscamed.domain.model.LLMExecutionHistory
import br.com.buscamed.domain.model.enumeration.ExecutionType
import br.com.buscamed.domain.repository.LLMExecutionHistoryRepository
import br.com.buscamed.domain.service.ImageStorageService
import br.com.buscamed.domain.service.LLMImageProcessService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant

/**
 * Caso de uso universal para processamento de imagens e extração de dados estruturados utilizando LLMs.
 *
 * Este caso de uso coordena a chamada para o serviço de processamento de imagem, registra
 * o histórico de execução (incluindo sucesso/falha e métricas de tokens) e realiza
 * o upload assíncrono da imagem original para o storage.
 *
 * @property executionHistoryRepository O repositório para salvar o histórico da execução.
 * @property llmProcessService O serviço de LLM responsável pelo processamento da imagem.
 * @property storageService O serviço de armazenamento para fazer o upload da imagem.
 */
class ProcessImageUseCase(
    private val executionHistoryRepository: LLMExecutionHistoryRepository,
    private val llmProcessService: LLMImageProcessService,
    private val storageService: ImageStorageService
) {
    private val uploadImageScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /**
     * Executa o processo de extração de dados da imagem.
     *
     * @param imageBytes O conteúdo da imagem em array de bytes.
     * @param mimeType O tipo MIME da imagem.
     * @param text O texto extraído da imagem pelo client.
     * @return O resultado processado pela LLM em formato de string JSON.
     * @throws BusinessException Se os bytes da imagem ou o tipo MIME forem nulos.
     */
    suspend operator fun invoke(
        imageBytes: ByteArray?,
        mimeType: String?,
        text: String?,
        clientProcessorVersion: String?
    ): String = withContext(Dispatchers.IO) {
        if (imageBytes == null) {
            throw BusinessException("É obrigatório informar uma imagem para processamento.")
        }

        if (mimeType.isNullOrBlank()) {
            throw BusinessException("É obrigatório informar o tipo de imagem para processamento.")
        }

        if (text.isNullOrBlank()) {
            throw BusinessException("É obrigatório informar o texto extraído da imagem.")
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

        try {
            val llmResult = llmProcessService.process(imageBytes, mimeType)
            inputTokens = llmResult.inputTokens
            outputTokens = llmResult.outputTokens
            resultText = llmResult.resultText
            prompt = llmResult.promptName

            resultText
        } catch (e: Exception) {
            executionSuccess = false
            throw e
        } finally {
            val history = LLMExecutionHistory(
                type = ExecutionType.IMAGE,
                inputText = text,
                inputTokens = inputTokens,
                outputTokens = outputTokens,
                result = resultText,
                success = executionSuccess,
                startDate = executionStart,
                endDate = Instant.now(),
                prompt = prompt,
                clientProcessorVersion = clientProcessorVersion
            )

            val historyId = executionHistoryRepository.save(history)

            uploadImageScope.launch {
                val path = storageService.upload(imageBytes, mimeType)
                executionHistoryRepository.updateImageStoragePath(historyId, path)
            }
        }
    }
}
