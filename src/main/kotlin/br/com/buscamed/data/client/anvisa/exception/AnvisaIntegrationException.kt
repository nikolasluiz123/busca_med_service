package br.com.buscamed.data.client.anvisa.exception

import br.com.buscamed.data.client.core.exception.IntegrationException

/**
 * Exceção lançada quando ocorre um erro na integração com a API da ANVISA.
 *
 * @param technicalMessage Mensagem técnica detalhando o erro.
 * @param statusCode Código de status HTTP associado ao erro (padrão: 502).
 * @param cause A causa raiz da exceção, se houver.
 */
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
