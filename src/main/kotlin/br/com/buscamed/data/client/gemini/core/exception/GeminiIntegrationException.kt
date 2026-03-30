package br.com.buscamed.data.client.gemini.core.exception

import br.com.buscamed.data.client.core.exception.IntegrationException

/**
 * Exceção lançada ao ocorrerem falhas durante a integração com a API do Google Gemini.
 *
 * @param userMessage A mensagem que pode ser exibida para o usuário final.
 * @param technicalMessage Detalhes do erro, ideal para logs e monitoramento.
 * @param statusCode Código HTTP simulado ou retornado pelo erro da API.
 * @param errorCode O código identificador mapeado na aplicação (ex: GeminiErrorCodes).
 * @param serviceName Nome do serviço originário que apresentou falha.
 * @param cause A causa ou exceção raiz em caso de wrapping.
 */
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
