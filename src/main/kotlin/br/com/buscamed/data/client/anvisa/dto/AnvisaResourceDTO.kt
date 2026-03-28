package br.com.buscamed.data.client.anvisa.dto

import kotlinx.serialization.Serializable

/**
 * DTO que representa um recurso individual dentro do conjunto de dados da ANVISA.
 *
 * @property format O formato do arquivo (ex: "CSV", "PDF").
 * @property url A URL para download do arquivo.
 */
@Serializable
data class AnvisaResourceDTO(
    val format: String,
    val url: String
)