package br.com.buscamed.data.mapper

import br.com.buscamed.api.v1.dto.response.LLMExecutionHistoryResponseDTO
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
        storageImagePath = storageImagePath
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
        storageImagePath = storageImagePath
    )
}

fun LLMExecutionHistory.toDTO(): LLMExecutionHistoryResponseDTO {
    return LLMExecutionHistoryResponseDTO(
        id = id,
        inputTokens = inputTokens,
        outputTokens = outputTokens,
        result = result,
        success = success,
        startDate = startDate.toString(),
        endDate = endDate.toString(),
        storageImagePath = storageImagePath
    )
}