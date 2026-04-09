package br.com.buscamed.data.repository

import br.com.buscamed.data.datasource.interfaces.AnvisaMedicationDataSource
import br.com.buscamed.data.mapper.toDocument
import br.com.buscamed.data.mapper.toDomain
import br.com.buscamed.domain.model.anvisa.AnvisaMedication
import br.com.buscamed.domain.model.anvisa.AnvisaMedicationLeaflet
import br.com.buscamed.domain.model.anvisa.enumeration.AnvisaMedicationLeafletIdentifier
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

    override suspend fun findPendingLeaflets(limit: Int): List<AnvisaMedication> {
        return dataSource.findPendingLeaflets(limit).map { it.toDomain() }
    }

    override suspend fun updateLeafletStatus(ggremCode: String, hasLeaflet: Boolean, leaflets: List<AnvisaMedicationLeaflet>) {
        dataSource.updateLeafletStatus(
            id = ggremCode,
            hasLeaflet = hasLeaflet,
            leaflets = leaflets.map { it.toDocument() }
        )
    }

    override suspend fun saveLeaflets(
        medicationIds: List<String>,
        leaflets: List<AnvisaMedicationLeaflet>,
        leafletIdentifier: AnvisaMedicationLeafletIdentifier?
    ) {
        dataSource.saveLeaflets(
            medicationIds = medicationIds,
            leaflets = leaflets.map { it.toDocument() },
            leafletIdentifier = leafletIdentifier?.name
        )
    }

    override suspend fun findMedicationsWithoutProfessionalLeafletsResume(limit: Int): List<AnvisaMedication> {
        return dataSource.findMedicationsWithoutProfessionalLeafletsResume(limit).map { it.toDomain() }
    }

    override suspend fun findMedicationsWithoutPatientLeafletsResume(limit: Int): List<AnvisaMedication> {
        return dataSource.findMedicationsWithoutPatientLeafletsResume(limit).map { it.toDomain() }
    }

    override suspend fun findLeafletBy(medicationId: String, leafletIdentifier: AnvisaMedicationLeafletIdentifier): AnvisaMedicationLeaflet? {
        return dataSource.findLeafletBy(medicationId, leafletIdentifier.name)?.toDomain()
    }
}