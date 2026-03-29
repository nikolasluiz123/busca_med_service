package br.com.buscamed.api.v1.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO que representa a resposta estruturada do processamento de uma prescrição médica.
 */
@Serializable
data class PrescriptionResponseDTO(
    val medicamentos: List<PrescriptionMedicationDTO> = emptyList()
)

/**
 * DTO que encapsula os dados extraídos de um único medicamento dentro de uma prescrição.
 */
@Serializable
data class PrescriptionMedicationDTO(
    val nome: String? = null,
    @SerialName("apresentacao_dosagem") val apresentacaoDosagem: ValueUnitDTO? = null,
    val dose: ValueUnitDTO? = null,
    val frequencia: FrequencyDTO? = null,
    val duracao: DurationDTO? = null,
    @SerialName("quantidade_total_prescrita") val quantidadeTotalPrescrita: ValueUnitDTO? = null
)

/**
 * DTO genérico para encapsular valores numéricos acompanhados de sua respectiva unidade de medida.
 */
@Serializable
data class ValueUnitDTO(
    val valor: Double? = null,
    val unidade: String? = null
)

/**
 * DTO que define a frequência de uso de um medicamento prescrito.
 */
@Serializable
data class FrequencyDTO(
    val intervalo: Double? = null,
    val unidade: String? = null,
    @SerialName("texto_orientacao") val textoOrientacao: String? = null
)

/**
 * DTO que define a duração do tratamento de um medicamento prescrito.
 */
@Serializable
data class DurationDTO(
    val valor: Double? = null,
    val unidade: String? = null,
    @SerialName("uso_continuo") val usoContinuo: Boolean = false
)