package br.com.buscamed.data.mapper

import br.com.buscamed.data.document.LLMExecutionHistoryDocument
import br.com.buscamed.domain.model.LLMExecutionHistory
import br.com.buscamed.domain.model.enumeration.ExecutionType

fun LLMExecutionHistory.toDocument(): LLMExecutionHistoryDocument {
    return LLMExecutionHistoryDocument(
        id = id,
        type = type.name,
        inputText = inputText,
        inputTokens = inputTokens,
        outputTokens = outputTokens,
        result = result,
        success = success,
        startDate = startDate,
        endDate = endDate,
        storageImagePath = storageImagePath,
        prompt = prompt,
        clientProcessorVersion = clientProcessorVersion,
        llmModel = llmModel
    )
}

fun LLMExecutionHistoryDocument.toDomain(): LLMExecutionHistory {
    return LLMExecutionHistory(
        id = id,
        type = ExecutionType.valueOf(type),
        inputText = inputText,
        inputTokens = inputTokens,
        outputTokens = outputTokens,
        result = result,
        success = success,
        startDate = startDate,
        endDate = endDate,
        storageImagePath = storageImagePath,
        prompt = prompt,
        clientProcessorVersion = clientProcessorVersion,
        llmModel = llmModel
    )
}