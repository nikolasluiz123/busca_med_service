package br.com.buscamed.domain.service

interface LeafletStorageService {
    suspend fun upload(medicationId: String, fileId: String, content: ByteArray): String
}