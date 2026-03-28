package br.com.buscamed.data.datasource.interfaces

import br.com.buscamed.data.document.AnvisaMedicationDocument

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
}