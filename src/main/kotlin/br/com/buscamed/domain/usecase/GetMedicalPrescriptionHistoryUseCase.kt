package br.com.buscamed.domain.usecase

import br.com.buscamed.domain.model.LLMExecutionHistory
import br.com.buscamed.domain.repository.LLMExecutionHistoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant

/**
 * Caso de uso responsável por obter o histórico de processamentos de prescrições médicas.
 *
 * @property repository Repositório de histórico de execuções.
 */
class GetMedicalPrescriptionHistoryUseCase(
    private val repository: LLMExecutionHistoryRepository
) {

    /**
     * Recupera a lista de histórico de execuções a partir de uma data fornecida.
     *
     * @param startDate Filtro de data de início.
     * @return Lista contendo os resultados históricos encontrados.
     */
    suspend operator fun invoke(startDate: Instant): List<LLMExecutionHistory> = withContext(Dispatchers.IO) {
        repository.findHistorySince(startDate)
    }
}