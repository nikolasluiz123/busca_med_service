package br.com.buscamed.data.document

import br.com.buscamed.core.config.serialization.serializer.InstantSerializer
import br.com.buscamed.data.document.core.FirestoreDocument
import com.google.cloud.firestore.annotation.DocumentId
import kotlinx.serialization.Serializable
import java.time.Instant

data class AnvisaMedicationLeafletDocument(
    @DocumentId
    override val id: String = "",
    val leafletStoragePath: String? = null,
    val leafletResume: String? = null,
    @Serializable(with = InstantSerializer::class)
    val leafletResumeCreatedAt: Instant? = null,
    @Serializable(with = InstantSerializer::class)
    val leafletResumeUpdatedAt: Instant? = null,
) : FirestoreDocument