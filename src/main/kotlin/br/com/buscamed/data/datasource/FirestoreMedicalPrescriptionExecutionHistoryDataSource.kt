package br.com.buscamed.data.datasource

import br.com.buscamed.data.datasource.core.BaseFirestoreDataSource
import br.com.buscamed.data.datasource.core.FirestoreSchema.EXECUTION_HISTORY_COLLECTION
import br.com.buscamed.data.datasource.core.FirestoreSchema.MEDICAL_PRESCRIPTION_COLLECTION
import br.com.buscamed.data.datasource.core.FirestoreSchema.RECORDS_COLLECTION
import br.com.buscamed.data.datasource.core.documentOrNew
import br.com.buscamed.data.datasource.interfaces.LLMExecutionHistoryDataSource
import br.com.buscamed.data.document.LLMExecutionHistoryDocument

@Suppress("BlockingMethodInNonBlockingContext")
class FirestoreMedicalPrescriptionExecutionHistoryDataSource : BaseFirestoreDataSource(), LLMExecutionHistoryDataSource {

    override suspend fun save(history: LLMExecutionHistoryDocument): String {
        val documentReference = db.collection(EXECUTION_HISTORY_COLLECTION)
            .document(MEDICAL_PRESCRIPTION_COLLECTION)
            .collection(RECORDS_COLLECTION)
            .documentOrNew(history.id)

        documentReference
            .set(history)
            .get()

        return documentReference.id
    }

    override suspend fun updateImageStoragePath(historyId: String, path: String) {
        db.collection(EXECUTION_HISTORY_COLLECTION)
            .document(MEDICAL_PRESCRIPTION_COLLECTION)
            .collection(RECORDS_COLLECTION)
            .document(historyId)
            .update(LLMExecutionHistoryDocument::storageImagePath.name, path)
            .get()
    }
}