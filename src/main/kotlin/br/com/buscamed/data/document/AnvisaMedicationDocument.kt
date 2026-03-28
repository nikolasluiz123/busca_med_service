package br.com.buscamed.data.document

import br.com.buscamed.data.document.core.FirestoreDocument
import com.google.cloud.firestore.annotation.DocumentId

/**
 * Representa o documento de um medicamento da ANVISA armazenado no Firestore.
 */
data class AnvisaMedicationDocument(
    @DocumentId
    override val id: String?,
    val activeIngredients: List<String>,
    val cnpj: String,
    val laboratory: String,
    val ean1: String?,
    val ean2: String?,
    val ean3: String?,
    val productName: String,
    val presentation: String,
    val therapeuticClass: String,
    val productType: String,
    val isHospitalRestriction: Boolean,
    val stripe: String
) : FirestoreDocument