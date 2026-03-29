package br.com.buscamed.domain.usecase

import br.com.buscamed.domain.exceptions.BusinessException
import br.com.buscamed.domain.model.LLMExecutionHistory
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
 * Caso de uso universal para processamento de imagens e extração de dados estruturados.
 */
class ProcessImageUseCase(
    private val executionHistoryRepository: LLMExecutionHistoryRepository,
    private val llmProcessService: LLMImageProcessService,
    private val storageService: ImageStorageService
) {
    private val uploadImageScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    suspend operator fun invoke(imageBytes: ByteArray?, mimeType: String?): String = withContext(Dispatchers.IO) {
        if (imageBytes == null) {
            throw BusinessException("É obrigatório informar uma imagem para processamento")
        }

        if (mimeType == null) {
            throw BusinessException("É obrigatório informar o tipo de imagem para processamento")
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
                inputTokens = inputTokens,
                outputTokens = outputTokens,
                result = resultText,
                success = executionSuccess,
                startDate = executionStart,
                endDate = Instant.now(),
                prompt = prompt
            )

            val historyId = executionHistoryRepository.save(history)

            uploadImageScope.launch {
                val path = storageService.upload(imageBytes, mimeType)
                executionHistoryRepository.updateImageStoragePath(historyId, path)
            }
        }
    }
}