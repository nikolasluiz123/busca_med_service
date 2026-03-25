package br.com.buscamed.data.client.gemini.core.exception

import br.com.buscamed.data.client.core.exception.IntegrationException
import io.ktor.http.*

class GeminiIntegrationException(
    userMessage: String,
    technicalMessage: String,
    statusCode: HttpStatusCode,
    errorCode: String,
    serviceName: String,
    cause: Throwable? = null
) : IntegrationException(
    serviceName = serviceName,
    technicalMessage = technicalMessage,
    userMessage = userMessage,
    cause = cause,
    statusCode = statusCode,
    errorCode = errorCode
)