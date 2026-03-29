package br.com.buscamed.domain.service

interface ImageStorageService {
    suspend fun upload(content: ByteArray, mimeType: String): String
    suspend fun download(fileName: String): ByteArray
}