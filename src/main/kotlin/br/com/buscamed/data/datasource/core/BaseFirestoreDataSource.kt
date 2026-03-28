package br.com.buscamed.data.datasource.core

import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.FirestoreOptions

abstract class BaseFirestoreDataSource {

    protected val db: Firestore by lazy {
        FirestoreOptions.newBuilder()
            .setDatabaseId("dev-db")
            .build()
            .service
    }
}