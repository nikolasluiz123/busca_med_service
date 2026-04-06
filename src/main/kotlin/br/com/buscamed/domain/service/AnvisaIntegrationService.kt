package br.com.buscamed.domain.service

import br.com.buscamed.domain.model.anvisa.AnvisaLeafletIds

/**
 * Contrato de serviço para a integração com a API de dados abertos da ANVISA.
 */
interface AnvisaIntegrationService {

    /**
     * Realiza o download do arquivo CSV de preços de medicamentos.
     *
     * @return Um [ByteArray] contendo os dados do arquivo CSV.
     */
    suspend fun downloadPricesCsv(): ByteArray

    suspend fun fetchLeafletIds(registerNumber: String): AnvisaLeafletIds?

    suspend fun downloadLeafletPdf(fileId: String): ByteArray
}
