package br.com.buscamed.data.client.anvisa

/**
 * Cliente responsável pela integração com os serviços de dados abertos da ANVISA.
 */
interface AnvisaIntegrationClient {

    /**
     * Realiza o download do arquivo CSV contendo os preços de medicamentos.
     *
     * @return Um array de bytes contendo o conteúdo do arquivo CSV.
     */
    suspend fun downloadPricesCsv(): ByteArray
}