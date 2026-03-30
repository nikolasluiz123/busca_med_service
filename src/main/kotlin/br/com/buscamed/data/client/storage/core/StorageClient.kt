package br.com.buscamed.data.client.storage.core

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Cliente base genérico que define o contrato de operações de armazenamento em nuvem.
 */
abstract class StorageClient {
    /** Formatador padrão para criar nomes de arquivos usando data e hora atuais. */
    protected val defaultFileNameDateFormater: DateTimeFormatter = DateTimeFormatter.ofPattern("dd_MM_yyyy_HH_mm_ss_SSS")

    /**
     * Realiza o upload de conteúdo binário para o armazenamento de arquivos.
     *
     * @param content Os bytes referentes ao arquivo.
     * @param mimeType O mimetype do arquivo (ex: image/jpeg).
     * @return O identificador do arquivo ou URI do recurso criado.
     */
    abstract suspend fun upload(content: ByteArray, mimeType: String): String

    /**
     * Recupera o conteúdo binário de um arquivo pelo seu nome/caminho no armazenamento.
     *
     * @param fileName O nome completo do arquivo ou o path de armazenamento.
     * @return Os dados do arquivo recuperado em formato byte array.
     */
    abstract suspend fun download(fileName: String): ByteArray

    /**
     * Retorna uma string que pode ser utilizada como o nome ou parte de um nome padrão
     * de um arquivo, gerada através da data e hora atual do sistema.
     */
    protected fun getDefaultFileName(): String {
        return LocalDateTime.now().format(defaultFileNameDateFormater)
    }
}
