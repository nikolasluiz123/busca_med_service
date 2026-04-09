package br.com.buscamed.data.datasource

import br.com.buscamed.data.datasource.core.BaseFirestoreLLMExecutionHistoryDataSource
import br.com.buscamed.data.datasource.core.FirestoreSchema.PATIENT_LEAFLET_COLLECTION
import com.google.cloud.firestore.Firestore

class FirestorePatientLeafletExecutionHistoryDataSource(
    db: Firestore
) : BaseFirestoreLLMExecutionHistoryDataSource(db, PATIENT_LEAFLET_COLLECTION)
