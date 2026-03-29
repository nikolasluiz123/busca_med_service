package br.com.buscamed.data.client.gemini.core.exception

import br.com.buscamed.data.client.core.exception.IntegrationException

class GeminiIntegrationException(
    userMessage: String,
    technicalMessage: String,
    statusCode: Int,
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