package br.com.buscamed.data.datasource.interfaces

import br.com.buscamed.data.document.AnvisaMedicationDocument
import br.com.buscamed.data.document.AnvisaMedicationLeafletDocument
import br.com.buscamed.domain.model.anvisa.AnvisaMedicationLeaflet

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
}