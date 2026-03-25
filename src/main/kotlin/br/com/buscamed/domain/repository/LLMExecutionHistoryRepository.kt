package br.com.buscamed.domain.repository

import br.com.buscamed.domain.model.LLMExecutionHistory

interface LLMExecutionHistoryRepository {
    suspend fun save(history: LLMExecutionHistory): String
    suspend fun updateImageStoragePath(historyId: String, path: String)
}