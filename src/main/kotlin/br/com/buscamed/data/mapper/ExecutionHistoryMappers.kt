package br.com.buscamed.data.mapper

import br.com.buscamed.data.document.LLMExecutionHistoryDocument
import br.com.buscamed.domain.model.LLMExecutionHistory

fun LLMExecutionHistory.toDocument(): LLMExecutionHistoryDocument {
    return LLMExecutionHistoryDocument(
        id = id,
        inputTokens = inputTokens,
        outputTokens = outputTokens,
        result = result,
        success = success,
        startDate = startDate,
        endDate = endDate,
        storageImagePath = storageImagePath,
        prompt = prompt
    )
}

fun LLMExecutionHistoryDocument.toDomain(): LLMExecutionHistory {
    return LLMExecutionHistory(
        id = id,
        inputTokens = inputTokens,
        outputTokens = outputTokens,
        result = result,
        success = success,
        startDate = startDate,
        endDate = endDate,
        storageImagePath = storageImagePath,
        prompt = prompt
    )
}