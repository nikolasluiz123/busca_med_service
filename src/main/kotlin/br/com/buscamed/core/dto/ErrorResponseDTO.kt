package br.com.buscamed.core.dto

import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class ErrorResponseDTO(
    val errorCode: String,
    val message: String,
    val timestamp: String = Instant.now().toString(),
    val path: String,
    val traceId: String? = null
)