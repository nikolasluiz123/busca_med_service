package br.com.buscamed.data.client.storage.core

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

abstract class StorageClient {
    protected val defaultFileNameDateFormater: DateTimeFormatter = DateTimeFormatter.ofPattern("dd_MM_yyyy_HH_mm_ss_SSS")

    abstract suspend fun upload(content: ByteArray, mimeType: String): String
    abstract suspend fun download(fileName: String): ByteArray

    protected fun getDefaultFileName(): String {
        return LocalDateTime.now().format(defaultFileNameDateFormater)
    }
}