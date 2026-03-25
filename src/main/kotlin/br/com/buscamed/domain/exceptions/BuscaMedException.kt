package br.com.buscamed.domain.exceptions

import io.ktor.http.HttpStatusCode

abstract class BuscaMedException(
    val userMessage: String,
    val httpStatusCode: HttpStatusCode,
    val errorCode: String,
    cause: Throwable? = null
) : RuntimeException(userMessage, cause)