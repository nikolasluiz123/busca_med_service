package br.com.buscamed.domain.service

interface AnvisaIntegrationService {
    suspend fun downloadPricesCsv(): ByteArray
}