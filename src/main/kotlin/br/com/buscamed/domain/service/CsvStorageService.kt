package br.com.buscamed.domain.service

/**
 * Contrato de serviço para o armazenamento de arquivos CSV.
 */
interface CsvStorageService {
    /**
     * Realiza o upload de um arquivo CSV.
     *
     * @param content O conteúdo do arquivo em um array de bytes.
     * @param mimeType O tipo MIME do arquivo.
     * @return O caminho do arquivo no serviço de armazenamento.
     */
    suspend fun upload(content: ByteArray, mimeType: String): String
}
