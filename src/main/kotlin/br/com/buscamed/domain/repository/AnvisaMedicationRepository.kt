package br.com.buscamed.domain.repository

import br.com.buscamed.domain.model.anvisa.AnvisaMedication

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
}
