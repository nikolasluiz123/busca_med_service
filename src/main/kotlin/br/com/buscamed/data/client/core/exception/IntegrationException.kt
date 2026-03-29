package br.com.buscamed.data.client.core.exception

import br.com.buscamed.domain.exceptions.BuscaMedException

open class IntegrationException(
    val serviceName: String,
    val technicalMessage: String,
    userMessage: String,
    statusCode: Int,
    errorCode: String,
    cause: Throwable? = null
) : BuscaMedException(userMessage, statusCode, errorCode, cause)