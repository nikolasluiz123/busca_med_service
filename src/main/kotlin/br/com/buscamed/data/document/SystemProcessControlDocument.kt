package br.com.buscamed.data.document

import br.com.buscamed.core.config.serialization.serializer.InstantSerializer
import br.com.buscamed.data.document.core.FirestoreDocument
import com.google.cloud.firestore.annotation.DocumentId
import kotlinx.serialization.Serializable
import java.time.Instant

/**
 * Documento Firestore para armazenamento de metadados de controle de processos do sistema.
 */
data class SystemProcessControlDocument(
    @DocumentId
    override val id: String? = null,
    val lastHash: String = "",
    val lastStorageFilePath: String = "",
    @Serializable(with = InstantSerializer::class)
    val lastUpdated: Instant = Instant.now()
) : FirestoreDocument