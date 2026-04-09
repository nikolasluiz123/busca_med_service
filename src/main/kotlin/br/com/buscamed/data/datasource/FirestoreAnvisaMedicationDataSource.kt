package br.com.buscamed.data.datasource

import br.com.buscamed.data.datasource.core.BaseFirestoreDataSource
import br.com.buscamed.data.datasource.core.FirestoreSchema.ANVISA_MEDICATIONS_COLLECTION
import br.com.buscamed.data.datasource.core.FirestoreSchema.ANVISA_MEDICATIONS_LEAFLET_COLLECTION
import br.com.buscamed.data.datasource.interfaces.AnvisaMedicationDataSource
import br.com.buscamed.data.document.AnvisaMedicationDocument
import br.com.buscamed.data.document.AnvisaMedicationLeafletDocument
import com.google.cloud.firestore.Firestore

class FirestoreAnvisaMedicationDataSource(
    db: Firestore
) : BaseFirestoreDataSource(db), AnvisaMedicationDataSource {

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

    override suspend fun findPendingLeaflets(limit: Int): List<AnvisaMedicationDocument> {
        val query = db.collection(ANVISA_MEDICATIONS_COLLECTION)
            .whereEqualTo(AnvisaMedicationDocument::hasLeaflet.name, false)
            .limit(limit)

        val snapshot = query.get().get()

        return snapshot.documents.mapNotNull {
            it.toObject(AnvisaMedicationDocument::class.java)
        }
    }

    override suspend fun updateLeafletStatus(id: String, hasLeaflet: Boolean, leaflets: List<AnvisaMedicationLeafletDocument>) {
        val medicationReference = db.collection(ANVISA_MEDICATIONS_COLLECTION).document(id)
        val leafletReferences = leaflets.map { leaflet ->
            medicationReference.collection(ANVISA_MEDICATIONS_LEAFLET_COLLECTION).document(leaflet.id) to leaflet
        }

        db.runTransaction { transaction ->
            transaction.update(
                medicationReference,
                AnvisaMedicationDocument::hasLeaflet.name,
                hasLeaflet
            )

            leafletReferences.forEach {
                transaction.set(it.first, it.second)
            }
        }.get()
    }

    override suspend fun saveLeaflets(
        medicationIds: List<String>,
        leaflets: List<AnvisaMedicationLeafletDocument>,
        leafletIdentifier: String?
    ) {
        val batch = db.batch()

        val medicationsCollection = db.collection(ANVISA_MEDICATIONS_COLLECTION)
        val medicationReferences = medicationIds.map { medicationsCollection.document(it) }

        medicationReferences.forEach { medicationReference ->
            val leafletsCollection = medicationReference.collection(ANVISA_MEDICATIONS_LEAFLET_COLLECTION)

            leaflets.forEach { leaflet ->
                val leafletReference = leafletsCollection.document(leaflet.id)
                batch.set(leafletReference, leaflet)

                when (leafletIdentifier) {
                    "PROFESSIONAL" -> {
                        batch.update(medicationReference, AnvisaMedicationDocument::hasLeafletProfessionalResume.name, true)
                    }

                    "PATIENT" -> {
                        batch.update(medicationReference, AnvisaMedicationDocument::hasLeafletPatientResume.name, true)
                    }

                    else -> {
                        batch.update(medicationReference, AnvisaMedicationDocument::hasLeafletProfessionalResume.name, true)
                        batch.update(medicationReference, AnvisaMedicationDocument::hasLeafletPatientResume.name, true)
                    }
                }
            }
        }

        batch.commit().get()
    }

    override suspend fun findMedicationsWithoutProfessionalLeafletsResume(limit: Int): List<AnvisaMedicationDocument> {
        return db.collection(ANVISA_MEDICATIONS_COLLECTION)
            .whereEqualTo(AnvisaMedicationDocument::hasLeaflet.name, true)
            .whereEqualTo(AnvisaMedicationDocument::hasLeafletProfessionalResume.name, false)
            .get()
            .get()
            .toObjects(AnvisaMedicationDocument::class.java)
    }

    override suspend fun findMedicationsWithoutPatientLeafletsResume(limit: Int): List<AnvisaMedicationDocument> {
        return db.collection(ANVISA_MEDICATIONS_COLLECTION)
            .whereEqualTo(AnvisaMedicationDocument::hasLeaflet.name, true)
            .whereEqualTo(AnvisaMedicationDocument::hasLeafletPatientResume.name, false)
            .get()
            .get()
            .toObjects(AnvisaMedicationDocument::class.java)
    }

    override suspend fun findLeafletBy(medicationId: String, leafletIdentifier: String): AnvisaMedicationLeafletDocument? {
        return db.collection(ANVISA_MEDICATIONS_COLLECTION)
            .document(medicationId)
            .collection(ANVISA_MEDICATIONS_LEAFLET_COLLECTION)
            .document(leafletIdentifier)
            .get()
            .get()
            .toObject(AnvisaMedicationLeafletDocument::class.java)
    }
}