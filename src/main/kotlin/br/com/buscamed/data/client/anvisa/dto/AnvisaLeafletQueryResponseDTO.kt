package br.com.buscamed.data.client.anvisa.dto

import kotlinx.serialization.Serializable

/**
 * DTO para representar a resposta da consulta de bulário da ANVISA.
 */
@Serializable
data class AnvisaLeafletQueryResponseDTO(
    val content: List<AnvisaLeafletContentDTO> = emptyList()
)
