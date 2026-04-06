package br.com.buscamed.domain.model.anvisa

import br.com.buscamed.domain.model.anvisa.enumeration.AnvisaMedicationLeafletIdentifier
import java.time.Instant

data class AnvisaMedicationLeaflet(
    val id: AnvisaMedicationLeafletIdentifier,
    val leafletStoragePath: String? = null,
    val leafletResume: String? = null,
    val leafletResumeCreatedAt: Instant? = null,
    val leafletResumeUpdatedAt: Instant? = null,
)