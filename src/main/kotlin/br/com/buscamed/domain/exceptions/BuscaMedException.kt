package br.com.buscamed.domain.exceptions

abstract class BuscaMedException(
    val userMessage: String,
    val statusCode: Int,
    val errorCode: String,
    cause: Throwable? = null
) : RuntimeException(userMessage, cause)