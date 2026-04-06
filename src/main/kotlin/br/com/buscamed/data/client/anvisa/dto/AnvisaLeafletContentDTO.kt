package br.com.buscamed.data.client.anvisa.dto

import kotlinx.serialization.Serializable

/**
 * DTO com os detalhes do medicamento e identificadores de bula.
 */
@Serializable
data class AnvisaLeafletContentDTO(
    val idBulaPacienteProtegido: String? = null,
    val idBulaProfissionalProtegido: String? = null
)