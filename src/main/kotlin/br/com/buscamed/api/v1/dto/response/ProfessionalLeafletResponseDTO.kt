package br.com.buscamed.api.v1.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProfessionalLeafletResponseDTO(
    @SerialName("indications_and_spectrum") val indicationsAndSpectrum: List<String>? = null,
    @SerialName("pharmacological_properties") val pharmacologicalProperties: String? = null,
    @SerialName("clinical_warnings") val clinicalWarnings: List<String>? = null,
    @SerialName("dosage_adjustments") val dosageAdjustments: List<String>? = null,
    @SerialName("drug_interactions") val drugInteractions: List<String>? = null,
    @SerialName("adverse_reactions") val adverseReactions: List<String>? = null,
    @SerialName("lab_test_interferences") val labTestInterferences: List<String>? = null
)