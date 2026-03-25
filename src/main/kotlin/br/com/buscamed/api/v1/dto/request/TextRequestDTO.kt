package br.com.buscamed.api.v1.dto.request

import kotlinx.serialization.Serializable

/**
 * DTO para requisições de processamento de texto.
 *
 * @property text O texto que será processado pela LLM.
 */
@Serializable
data class TextRequestDTO(val text: String)