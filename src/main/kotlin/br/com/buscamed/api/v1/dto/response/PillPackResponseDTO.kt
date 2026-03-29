package br.com.buscamed.api.v1.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO que representa a resposta estruturada do processamento de uma cartela de comprimidos.
 */
@Serializable
data class PillPackResponseDTO(
    @SerialName("nome_medicamento") val nomeMedicamento: String? = null,
    val componentes: List<PillPackComponentDTO> = emptyList(),
    val uso: PillPackUsageDTO? = null,
    val indicacoes: List<String> = emptyList(),
    @SerialName("data_validade") val dataValidade: String? = null,
    val lote: String? = null
)

/**
 * DTO que encapsula a composição de um princípio ativo presente na cartela de comprimidos.
 */
@Serializable
data class PillPackComponentDTO(
    @SerialName("principio_ativo") val principioAtivo: String,
    @SerialName("dosagem_valor") val dosagemValor: Double? = null,
    @SerialName("dosagem_unidade") val dosagemUnidade: String? = null
)

/**
 * DTO que define as vias de administração e restrições de uso impressas na cartela.
 */
@Serializable
data class PillPackUsageDTO(
    @SerialName("vias_administracao") val viasAdministracao: List<String> = emptyList(),
    @SerialName("restricoes_idade") val restricoesIdade: List<String> = emptyList()
)