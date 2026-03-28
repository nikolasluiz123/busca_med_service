package br.com.buscamed.data.repository

import br.com.buscamed.data.datasource.interfaces.AnvisaMedicationDataSource
import br.com.buscamed.data.mapper.toDocument
import br.com.buscamed.domain.model.anvisa.AnvisaMedication
import br.com.buscamed.domain.repository.AnvisaMedicationRepository

/**
 * Implementação de [AnvisaMedicationRepository].
 */
class AnvisaMedicationRepositoryImpl(
    private val dataSource: AnvisaMedicationDataSource
) : AnvisaMedicationRepository {

    override suspend fun saveAll(medications: List<AnvisaMedication>) {
        dataSource.saveAll(medications.map { it.toDocument() })
    }
}