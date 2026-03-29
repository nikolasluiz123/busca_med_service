package br.com.buscamed.data.client.anvisa.exception

import br.com.buscamed.data.client.core.exception.IntegrationException

class AnvisaIntegrationException(
    technicalMessage: String,
    statusCode: Int = 502,
    cause: Throwable? = null
) : IntegrationException(
    serviceName = "AnvisaIntegrationClient",
    technicalMessage = technicalMessage,
    userMessage = "Ocorreu uma instabilidade ao consultar a base de dados da ANVISA. Tente novamente mais tarde.",
    statusCode = statusCode,
    errorCode = "ANVISA_INTEGRATION_ERROR",
    cause = cause
)