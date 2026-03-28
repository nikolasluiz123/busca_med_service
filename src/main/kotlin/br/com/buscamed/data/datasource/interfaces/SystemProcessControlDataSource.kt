package br.com.buscamed.data.datasource.interfaces

import br.com.buscamed.data.document.SystemProcessControlDocument

interface SystemProcessControlDataSource {
    suspend fun findById(id: String): SystemProcessControlDocument?
    suspend fun save(document: SystemProcessControlDocument)
}