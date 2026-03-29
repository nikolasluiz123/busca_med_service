package br.com.buscamed.data.document

import br.com.buscamed.core.config.serialization.serializer.InstantSerializer
import br.com.buscamed.data.document.core.FirestoreDocument
import com.google.cloud.firestore.annotation.DocumentId
import kotlinx.serialization.Serializable
import java.time.Instant

data class LLMExecutionHistoryDocument(
    @DocumentId
    override val id: String? = null,
    val inputTokens: Int = 0,
    val outputTokens: Int = 0,
    val result: String? = null,
    val success: Boolean = false,
    @Serializable(with = InstantSerializer::class)
    val startDate: Instant = Instant.now(),
    @Serializable(with = InstantSerializer::class)
    val endDate: Instant = Instant.now(),
    val storageImagePath: String? = null,
    val prompt: String = ""
) : FirestoreDocument