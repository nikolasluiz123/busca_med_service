package br.com.buscamed.domain.usecase

import br.com.buscamed.data.client.gemini.core.client.GeminiImageProcessClient
import br.com.buscamed.data.client.storage.google.core.ImagesGoogleStorageClient
import br.com.buscamed.domain.exceptions.BusinessException
import br.com.buscamed.domain.model.LLMExecutionHistory
import br.com.buscamed.domain.repository.LLMExecutionHistoryRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonObject
import java.time.Instant

abstract class BaseProcessImageUseCase(
    private val executionHistoryRepository: LLMExecutionHistoryRepository,
    private val geminiClient: GeminiImageProcessClient,
    private val storageClient: ImagesGoogleStorageClient
) {
    private val uploadImageScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    suspend operator fun invoke(imageBytes: ByteArray?, mimeType: String?): JsonObject = withContext(Dispatchers.IO) {
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
        var resultJson: JsonObject? = null

        try {
            val geminiResult = geminiClient.process(imageBytes, mimeType)
            inputTokens = geminiResult.inputTokens
            outputTokens = geminiResult.outputTokens
            resultJson = geminiResult.json
            resultJson
        } catch (e: Exception) {
            executionSuccess = false
            throw e
        } finally {
            val history = LLMExecutionHistory(
                inputTokens = inputTokens,
                outputTokens = outputTokens,
                result = resultJson?.toString(),
                success = executionSuccess,
                startDate = executionStart,
                endDate = Instant.now(),
            )

            val historyId = executionHistoryRepository.save(history)

            uploadImageScope.launch {
                val path = storageClient.upload(imageBytes, mimeType)
                executionHistoryRepository.updateImageStoragePath(historyId, path)
            }
        }
    }
}