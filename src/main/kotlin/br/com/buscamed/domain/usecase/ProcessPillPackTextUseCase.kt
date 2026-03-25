package br.com.buscamed.domain.usecase

import br.com.buscamed.data.client.gemini.core.result.GeminiResult
import br.com.buscamed.data.client.gemini.core.client.GeminiTextProcessClient
import br.com.buscamed.data.client.gemini.text.GeminiMedicalPrescriptionTextProcessClient
import br.com.buscamed.data.client.gemini.text.GeminiPillPackTextProcessClient
import br.com.buscamed.domain.model.LLMExecutionHistory
import br.com.buscamed.domain.repository.LLMExecutionHistoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonObject
import java.time.Instant

class ProcessPillPackTextUseCase(
    private val executionHistoryRepository: LLMExecutionHistoryRepository,
    private val geminiClient: GeminiPillPackTextProcessClient
) {

    suspend operator fun invoke(text: String): JsonObject = withContext(Dispatchers.IO) {
        var geminiResult: GeminiResult? = null
        var executionSuccess = true
        val executionStart = Instant.now()

        try {
            geminiResult = geminiClient.process(text)
            geminiResult.json
        } catch (e: Exception) {
            executionSuccess = false
            throw e
        } finally {
            val history = LLMExecutionHistory(
                inputTokens = geminiResult?.inputTokens ?: 0,
                outputTokens = geminiResult?.outputTokens ?: 0,
                result = geminiResult?.json?.toString(),
                success = executionSuccess,
                startDate = executionStart,
                endDate = Instant.now(),
            )

            executionHistoryRepository.save(history)
        }
    }
}