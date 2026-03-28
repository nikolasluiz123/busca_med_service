package br.com.buscamed.data.repository

import br.com.buscamed.data.datasource.interfaces.SystemProcessControlDataSource
import br.com.buscamed.data.mapper.toDocument
import br.com.buscamed.data.mapper.toDomain
import br.com.buscamed.domain.model.system.SystemProcessControl
import br.com.buscamed.domain.repository.SystemProcessControlRepository

class SystemProcessControlRepositoryImpl(
    private val dataSource: SystemProcessControlDataSource
) : SystemProcessControlRepository {

    override suspend fun findById(id: String): SystemProcessControl? {
        return dataSource.findById(id)?.toDomain()
    }

    override suspend fun save(control: SystemProcessControl) {
        dataSource.save(control.toDocument())
    }
}