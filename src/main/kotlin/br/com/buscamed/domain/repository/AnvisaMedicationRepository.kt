package br.com.buscamed.domain.repository

import br.com.buscamed.domain.model.anvisa.AnvisaMedication
import br.com.buscamed.domain.model.anvisa.AnvisaMedicationLeaflet
import br.com.buscamed.domain.model.anvisa.enumeration.AnvisaMedicationLeafletIdentifier

/**
 * Contrato de repositório para gerenciar a persistência de medicamentos da ANVISA.
 */
interface AnvisaMedicationRepository {
    
    /**
     * Salva uma lista de medicamentos na base de dados.
     *
     * @param medications Lista de entidades [AnvisaMedication] a serem persistidas.
     */
    suspend fun saveAll(medications: List<AnvisaMedication>)

    suspend fun findPendingLeaflets(limit: Int): List<AnvisaMedication>

    suspend fun updateLeafletStatus(ggremCode: String, hasLeaflet: Boolean, leaflets: List<AnvisaMedicationLeaflet>)

    suspend fun saveLeaflets(
        medicationIds: List<String>,
        leaflets: List<AnvisaMedicationLeaflet>,
        leafletIdentifier: AnvisaMedicationLeafletIdentifier?
    )

    suspend fun findMedicationsWithoutProfessionalLeafletsResume(limit: Int): List<AnvisaMedication>

    suspend fun findMedicationsWithoutPatientLeafletsResume(limit: Int): List<AnvisaMedication>

    suspend fun findLeafletBy(medicationId: String, leafletIdentifier: AnvisaMedicationLeafletIdentifier): AnvisaMedicationLeaflet?
}
