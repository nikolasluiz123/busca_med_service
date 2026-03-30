package br.com.buscamed.domain.usecase

import br.com.buscamed.domain.model.LLMExecutionHistory
import br.com.buscamed.domain.repository.LLMExecutionHistoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant

/**
 * Caso de uso universal para resgatar históricos de chamadas às LLMs.
 *
 * @property repository O repositório responsável por recuperar os registros de histórico.
 */
class GetLLMExecutionHistoryUseCase(
    private val repository: LLMExecutionHistoryRepository
) {

    /**
     * Recupera os históricos de execução de LLM a partir de uma data específica.
     *
     * @param startDate A data inicial para a busca dos registros.
     * @return Uma lista de [LLMExecutionHistory] correspondentes à busca.
     */
    suspend operator fun invoke(startDate: Instant): List<LLMExecutionHistory> = withContext(Dispatchers.IO) {
        repository.findHistorySince(startDate)
    }
}
