package br.com.buscamed.domain.usecase

import br.com.buscamed.domain.model.LLMExecutionHistory
import br.com.buscamed.domain.repository.LLMExecutionHistoryRepository
import br.com.buscamed.domain.service.LLMTextProcessService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant

/**
 * Caso de uso universal para extração estruturada a partir de texto base.
 */
class ProcessTextUseCase(
    private val executionHistoryRepository: LLMExecutionHistoryRepository,
    private val llmProcessService: LLMTextProcessService
) {

    suspend operator fun invoke(text: String): String = withContext(Dispatchers.IO) {
        var executionSuccess = true
        val executionStart = Instant.now()
        var inputTokens = 0
        var outputTokens = 0
        var resultText = "{}"
        var prompt = ""

        try {
            val llmResult = llmProcessService.process(text)
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

            executionHistoryRepository.save(history)
        }
    }
}