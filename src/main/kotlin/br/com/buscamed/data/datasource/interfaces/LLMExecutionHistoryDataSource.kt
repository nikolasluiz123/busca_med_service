package br.com.buscamed.data.datasource.interfaces

import br.com.buscamed.data.document.LLMExecutionHistoryDocument
import java.time.Instant

interface LLMExecutionHistoryDataSource {
    suspend fun save(history: LLMExecutionHistoryDocument): String
    suspend fun updateImageStoragePath(historyId: String, path: String)
    suspend fun findHistorySince(startDate: Instant): List<LLMExecutionHistoryDocument>
}