package br.com.buscamed.data.datasource

import br.com.buscamed.data.datasource.core.BaseFirestoreLLMExecutionHistoryDataSource
import br.com.buscamed.data.datasource.core.FirestoreSchema.PILL_PACK_COLLECTION
import com.google.cloud.firestore.Firestore

class FirestorePillPackExecutionHistoryDataSource(
    db: Firestore
) : BaseFirestoreLLMExecutionHistoryDataSource(db, PILL_PACK_COLLECTION)