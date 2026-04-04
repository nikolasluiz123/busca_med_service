package br.com.buscamed.api.v1.mapper

import br.com.buscamed.api.v1.dto.response.LLMExecutionHistoryResponseDTO
import br.com.buscamed.domain.model.LLMExecutionHistory

fun LLMExecutionHistory.toDTO(): LLMExecutionHistoryResponseDTO {
    return LLMExecutionHistoryResponseDTO(
        id = id,
        type = type.name,
        inputText = inputText,
        inputTokens = inputTokens,
        outputTokens = outputTokens,
        result = result,
        success = success,
        startDate = startDate.toString(),
        endDate = endDate.toString(),
        storageImagePath = storageImagePath,
        prompt = prompt,
        clientProcessorVersion = clientProcessorVersion
    )
}
