package br.com.buscamed.data.repository

import br.com.buscamed.data.datasource.interfaces.LLMExecutionHistoryDataSource
import br.com.buscamed.data.mapper.toDocument
import br.com.buscamed.data.mapper.toDomain
import br.com.buscamed.domain.model.LLMExecutionHistory
import br.com.buscamed.domain.repository.LLMExecutionHistoryRepository
import java.time.Instant

abstract class BaseLLMExecutionHistoryRepositoryImpl(
    private val dataSource: LLMExecutionHistoryDataSource
): LLMExecutionHistoryRepository {

    override suspend fun save(history: LLMExecutionHistory): String {
        return dataSource.save(history.toDocument())
    }

    override suspend fun updateImageStoragePath(historyId: String, path: String) {
        dataSource.updateImageStoragePath(historyId, path)
    }

    override suspend fun findHistorySince(startDate: Instant): List<LLMExecutionHistory> {
        return dataSource.findHistorySince(startDate).map { it.toDomain() }
    }
}