package br.com.buscamed.data.datasource

import br.com.buscamed.data.datasource.core.BaseFirestoreLLMExecutionHistoryDataSource
import br.com.buscamed.data.datasource.core.FirestoreSchema.PROFESSIONAL_LEAFLET_COLLECTION
import com.google.cloud.firestore.Firestore

class FirestoreProfessionalLeafletExecutionHistoryDataSource(
    db: Firestore
) : BaseFirestoreLLMExecutionHistoryDataSource(db, PROFESSIONAL_LEAFLET_COLLECTION)
