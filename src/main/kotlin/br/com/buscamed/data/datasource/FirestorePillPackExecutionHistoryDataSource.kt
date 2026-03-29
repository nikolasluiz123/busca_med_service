package br.com.buscamed.data.datasource

import br.com.buscamed.data.datasource.core.BaseFirestoreDataSource
import br.com.buscamed.data.datasource.core.FirestoreSchema.EXECUTION_HISTORY_COLLECTION
import br.com.buscamed.data.datasource.core.FirestoreSchema.PILL_PACK_COLLECTION
import br.com.buscamed.data.datasource.core.FirestoreSchema.RECORDS_COLLECTION
import br.com.buscamed.data.datasource.core.documentOrNew
import br.com.buscamed.data.datasource.interfaces.LLMExecutionHistoryDataSource
import br.com.buscamed.data.document.LLMExecutionHistoryDocument
import java.time.Instant

@Suppress("BlockingMethodInNonBlockingContext")
class FirestorePillPackExecutionHistoryDataSource : BaseFirestoreDataSource(), LLMExecutionHistoryDataSource {

    override suspend fun save(history: LLMExecutionHistoryDocument): String {
        val documentReference = db.collection(EXECUTION_HISTORY_COLLECTION)
            .document(PILL_PACK_COLLECTION)
            .collection(RECORDS_COLLECTION)
            .documentOrNew(history.id)

        documentReference
            .set(history)
            .get()

        return documentReference.id
    }

    override suspend fun updateImageStoragePath(historyId: String, path: String) {
        db.collection(EXECUTION_HISTORY_COLLECTION)
            .document(PILL_PACK_COLLECTION)
            .collection(RECORDS_COLLECTION)
            .document(historyId)
            .update(LLMExecutionHistoryDocument::storageImagePath.name, path)
            .get()
    }

    override suspend fun findHistorySince(startDate: Instant): List<LLMExecutionHistoryDocument> {
        val query = db.collection(EXECUTION_HISTORY_COLLECTION)
            .document(PILL_PACK_COLLECTION)
            .collection(RECORDS_COLLECTION)
            .whereGreaterThanOrEqualTo(LLMExecutionHistoryDocument::startDate.name, startDate)

        val snapshot = query.get().get()

        return snapshot.documents.mapNotNull {
            it.toObject(LLMExecutionHistoryDocument::class.java)
        }
    }

    override suspend fun findHistoryById(historyId: String): LLMExecutionHistoryDocument? {
        val documentSnapshot = db.collection(EXECUTION_HISTORY_COLLECTION)
            .document(PILL_PACK_COLLECTION)
            .collection(RECORDS_COLLECTION)
            .document(historyId)
            .get()
            .get()
            
        return documentSnapshot.toObject(LLMExecutionHistoryDocument::class.java)
    }
}