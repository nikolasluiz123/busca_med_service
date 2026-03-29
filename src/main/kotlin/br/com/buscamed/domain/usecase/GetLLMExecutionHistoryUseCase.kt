package br.com.buscamed.domain.usecase

import br.com.buscamed.domain.model.LLMExecutionHistory
import br.com.buscamed.domain.repository.LLMExecutionHistoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant

/**
 * Caso de uso universal para resgatar históricos de chamadas às LLMs.
 */
class GetLLMExecutionHistoryUseCase(
    private val repository: LLMExecutionHistoryRepository
) {

    suspend operator fun invoke(startDate: Instant): List<LLMExecutionHistory> = withContext(Dispatchers.IO) {
        repository.findHistorySince(startDate)
    }
}