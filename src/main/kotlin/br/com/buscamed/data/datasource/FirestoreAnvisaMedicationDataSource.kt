package br.com.buscamed.data.datasource

import br.com.buscamed.data.datasource.core.BaseFirestoreDataSource
import br.com.buscamed.data.datasource.core.FirestoreSchema.ANVISA_MEDICATIONS_COLLECTION
import br.com.buscamed.data.datasource.interfaces.AnvisaMedicationDataSource
import br.com.buscamed.data.document.AnvisaMedicationDocument
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Implementação de [AnvisaMedicationDataSource] utilizando Firestore.
 */
class FirestoreAnvisaMedicationDataSource : BaseFirestoreDataSource(), AnvisaMedicationDataSource {

    override suspend fun saveAll(medications: List<AnvisaMedicationDocument>) {
        val collection = db.collection(ANVISA_MEDICATIONS_COLLECTION)
        val batches = medications.chunked(500)

        batches.forEach { batchList ->
            val batch = db.batch()
            batchList.forEach { doc ->
                val docRef = collection.document(doc.id!!)
                batch.set(docRef, doc)
            }
            batch.commit().get()
        }
    }
}