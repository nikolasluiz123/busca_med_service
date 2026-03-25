package br.com.buscamed.data.document

import br.com.buscamed.core.config.serialization.serializer.InstantSerializer
import br.com.buscamed.data.document.core.FirestoreDocument
import com.google.cloud.firestore.annotation.DocumentId
import kotlinx.serialization.Serializable
import java.time.Instant

data class LLMExecutionHistoryDocument(
    @DocumentId
    override val id: String?,
    val inputTokens: Int,
    val outputTokens: Int,
    val result: String?,
    val success: Boolean,
    @Serializable(with = InstantSerializer::class)
    val startDate: Instant,
    @Serializable(with = InstantSerializer::class)
    val endDate: Instant,
    val storageImagePath: String?
) : FirestoreDocument