package br.com.buscamed.api.v1.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PatientLeafletResponseDTO(
    @SerialName("indications") val indications: List<String>? = null,
    @SerialName("mechanism_of_action") val mechanismOfAction: String? = null,
    @SerialName("contraindications") val contraindications: List<String>? = null,
    @SerialName("precautions_and_warnings") val precautionsAndWarnings: List<String>? = null,
    @SerialName("interactions_to_avoid") val interactionsToAvoid: List<String>? = null,
    @SerialName("how_to_use") val howToUse: String? = null,
    @SerialName("missed_dose") val missedDose: String? = null,
    @SerialName("common_side_effects") val commonSideEffects: List<String>? = null
)