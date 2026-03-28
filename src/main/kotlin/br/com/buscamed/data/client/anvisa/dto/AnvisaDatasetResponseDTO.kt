package br.com.buscamed.data.client.anvisa.dto

import kotlinx.serialization.Serializable

/**
 * DTO que representa a resposta da API de conjuntos de dados da ANVISA.
 *
 * @property resources Lista de recursos disponíveis no conjunto de dados.
 */
@Serializable
data class AnvisaDatasetResponseDTO(
    val resources: List<AnvisaResourceDTO>
)