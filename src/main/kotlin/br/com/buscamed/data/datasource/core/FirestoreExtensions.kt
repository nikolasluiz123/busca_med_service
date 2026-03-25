package br.com.buscamed.data.datasource.core

import com.google.cloud.firestore.CollectionReference
import com.google.cloud.firestore.DocumentReference

fun CollectionReference.documentOrNew(id: String?): DocumentReference {
    return id?.let { this.document(id) } ?: this.document()
}