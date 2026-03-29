package br.com.buscamed.data.datasource

import br.com.buscamed.data.datasource.core.BaseFirestoreLLMExecutionHistoryDataSource
import br.com.buscamed.data.datasource.core.FirestoreSchema.MEDICAL_PRESCRIPTION_COLLECTION
import com.google.cloud.firestore.Firestore

class FirestoreMedicalPrescriptionExecutionHistoryDataSource(
    db: Firestore
) : BaseFirestoreLLMExecutionHistoryDataSource(db, MEDICAL_PRESCRIPTION_COLLECTION)