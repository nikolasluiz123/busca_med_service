package br.com.buscamed.domain.service

interface CsvStorageService {
    suspend fun upload(content: ByteArray, mimeType: String): String
}