package br.com.buscamed.domain.service

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
}
