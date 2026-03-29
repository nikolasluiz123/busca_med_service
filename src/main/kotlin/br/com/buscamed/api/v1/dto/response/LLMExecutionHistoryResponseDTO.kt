package br.com.buscamed.api.v1.dto.response

import kotlinx.serialization.Serializable

/**
 * DTO que representa os dados de resposta do histórico de execução da LLM.
 *
 * @property id Identificador único da execução.
 * @property inputTokens Quantidade de tokens enviados no prompt.
 * @property outputTokens Quantidade de tokens retornados.
 * @property result Resultado em string retornado pela LLM.
 * @property success Indica se a execução foi finalizada com sucesso.
 * @property startDate Data e hora de início da execução (formato ISO-8601).
 * @property endDate Data e hora de término da execução (formato ISO-8601).
 * @property storageImagePath Caminho do arquivo de imagem salvo no storage.
 */
@Serializable
data class LLMExecutionHistoryResponseDTO(
    val id: String?,
    val inputTokens: Int,
    val outputTokens: Int,
    val result: String?,
    val success: Boolean,
    val startDate: String,
    val endDate: String,
    val storageImagePath: String?,
    val prompt: String
)