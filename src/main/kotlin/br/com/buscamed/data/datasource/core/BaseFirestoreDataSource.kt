package br.com.buscamed.data.datasource.core

import com.google.cloud.firestore.Firestore

abstract class BaseFirestoreDataSource(
    protected val db: Firestore
)