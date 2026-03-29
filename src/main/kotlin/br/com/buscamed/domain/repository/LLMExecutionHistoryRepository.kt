package br.com.buscamed.domain.repository

import br.com.buscamed.domain.model.LLMExecutionHistory
import java.time.Instant

interface LLMExecutionHistoryRepository {
    suspend fun save(history: LLMExecutionHistory): String
    suspend fun updateImageStoragePath(historyId: String, path: String)
    suspend fun findHistorySince(startDate: Instant): List<LLMExecutionHistory>
    suspend fun findHistoryById(historyId: String): LLMExecutionHistory?
}