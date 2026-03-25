package br.com.buscamed.data.datasource.interfaces

import br.com.buscamed.data.document.LLMExecutionHistoryDocument

interface LLMExecutionHistoryDataSource {
    suspend fun save(history: LLMExecutionHistoryDocument): String
    suspend fun updateImageStoragePath(historyId: String, path: String)
}