package br.com.buscamed.data.datasource.interfaces

import br.com.buscamed.data.document.AnvisaMedicationDocument
import br.com.buscamed.data.document.AnvisaMedicationLeafletDocument

/**
 * Fonte de dados para operações de medicamentos da ANVISA.
 */
interface AnvisaMedicationDataSource {

    /**
     * Salva uma lista de documentos de medicamentos no banco de dados.
     *
     * @param medications Lista de documentos a serem salvos.
     */
    suspend fun saveAll(medications: List<AnvisaMedicationDocument>)

    /**
     * Recupera documentos que ainda não possuem bula processada.
     */
    suspend fun findPendingLeaflets(limit: Int): List<AnvisaMedicationDocument>

    /**
     * Atualiza apenas o status da bula de um medicamento específico.
     */
    suspend fun updateLeafletStatus(id: String, hasLeaflet: Boolean, leaflets: List<AnvisaMedicationLeafletDocument>)

    suspend fun saveLeaflets(
        medicationIds: List<String>,
        leaflets: List<AnvisaMedicationLeafletDocument>,
        leafletIdentifier: String?
    )

    suspend fun findMedicationsWithoutProfessionalLeafletsResume(limit: Int): List<AnvisaMedicationDocument>

    suspend fun findMedicationsWithoutPatientLeafletsResume(limit: Int): List<AnvisaMedicationDocument>

    suspend fun findLeafletBy(medicationId: String, leafletIdentifier: String): AnvisaMedicationLeafletDocument?
}