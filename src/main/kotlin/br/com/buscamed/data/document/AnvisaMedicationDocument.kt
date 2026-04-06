package br.com.buscamed.data.document

import br.com.buscamed.data.document.core.FirestoreDocument
import com.google.cloud.firestore.annotation.DocumentId

/**
 * Representa o documento de um medicamento da ANVISA armazenado no Firestore.
 */
data class AnvisaMedicationDocument(
    @DocumentId
    override val id: String = "",
    val registerNumber: String = "",
    val activeIngredients: List<String> = emptyList(),
    val cnpj: String = "",
    val laboratory: String = "",
    val ean1: String? = null,
    val ean2: String? = null,
    val ean3: String? = null,
    val productName: String = "",
    val presentation: String = "",
    val therapeuticClass: String = "",
    val productType: String = "",
    val hospitalRestriction: Boolean = false,
    val stripe: String = "",
    val hasLeaflet: Boolean = false
) : FirestoreDocument