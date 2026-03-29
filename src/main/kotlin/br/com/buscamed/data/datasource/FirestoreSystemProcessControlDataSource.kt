package br.com.buscamed.data.datasource

import br.com.buscamed.data.datasource.core.BaseFirestoreDataSource
import br.com.buscamed.data.datasource.core.FirestoreSchema.SYSTEM_PROCESS_CONTROL_COLLECTION
import br.com.buscamed.data.datasource.interfaces.SystemProcessControlDataSource
import br.com.buscamed.data.document.SystemProcessControlDocument
import com.google.cloud.firestore.Firestore

class FirestoreSystemProcessControlDataSource(
    db: Firestore
) : BaseFirestoreDataSource(db), SystemProcessControlDataSource {

    override suspend fun findById(id: String): SystemProcessControlDocument? {
        val documentSnapshot = db.collection(SYSTEM_PROCESS_CONTROL_COLLECTION).document(id).get().get()

        return if (documentSnapshot.exists()) {
            documentSnapshot.toObject(SystemProcessControlDocument::class.java)
        } else {
            null
        }
    }

    override suspend fun save(document: SystemProcessControlDocument) {
        db.collection(SYSTEM_PROCESS_CONTROL_COLLECTION)
            .document(document.id!!)
            .set(document)
            .get()
    }
}