package br.com.buscamed.api.v1.mapper

import br.com.buscamed.api.v1.dto.response.LLMExecutionHistoryResponseDTO
import br.com.buscamed.domain.model.LLMExecutionHistory

/**
 * Converte a entidade de domínio [LLMExecutionHistory] em um DTO de resposta para a API.
 */
fun LLMExecutionHistory.toDTO(): LLMExecutionHistoryResponseDTO {
    return LLMExecutionHistoryResponseDTO(
        id = id,
        inputTokens = inputTokens,
        outputTokens = outputTokens,
        result = result,
        success = success,
        startDate = startDate.toString(),
        endDate = endDate.toString(),
        storageImagePath = storageImagePath,
        prompt = prompt
    )
}
