package br.com.buscamed.domain.service

/**
 * Contrato de serviço para o armazenamento de imagens.
 */
interface ImageStorageService {
    /**
     * Realiza o upload de uma imagem.
     *
     * @param content O conteúdo da imagem em um array de bytes.
     * @param mimeType O tipo MIME da imagem.
     * @return O caminho da imagem no serviço de armazenamento.
     */
    suspend fun upload(content: ByteArray, mimeType: String): String
    /**
     * Realiza o download de uma imagem.
     *
     * @param fileName O nome do arquivo a ser baixado.
     * @return O conteúdo da imagem em um array de bytes.
     */
    suspend fun download(fileName: String): ByteArray
}
