package br.com.buscamed.data.client.core.exception

import br.com.buscamed.domain.exceptions.BuscaMedException
import io.ktor.http.HttpStatusCode

open class IntegrationException(
    val serviceName: String,
    val technicalMessage: String,
    userMessage: String,
    statusCode: HttpStatusCode,
    errorCode: String,
    cause: Throwable? = null
) : BuscaMedException(userMessage, statusCode, errorCode, cause)