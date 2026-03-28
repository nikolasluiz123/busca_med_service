package br.com.buscamed.domain.repository

import br.com.buscamed.domain.model.system.SystemProcessControl

interface SystemProcessControlRepository {
    suspend fun findById(id: String): SystemProcessControl?
    suspend fun save(control: SystemProcessControl)
}