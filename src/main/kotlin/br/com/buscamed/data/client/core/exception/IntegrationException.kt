package br.com.buscamed.data.client.core.exception

import br.com.buscamed.domain.exceptions.BuscaMedException

/**
 * Exceção base para erros de integração com serviços externos.
 *
 * Esta classe deve ser estendida por exceções específicas de cada integração.
 *
 * @property serviceName O nome do serviço com o qual a integração falhou.
 * @property technicalMessage Mensagem de erro técnica, não destinada ao usuário final.
 * @param userMessage Mensagem de erro amigável para o usuário final.
 * @param statusCode O código de status HTTP da resposta que causou o erro.
 * @param errorCode Um código de erro interno para identificar a falha.
 * @param cause A exceção original que causou o erro de integração.
 */
open class IntegrationException(
    val serviceName: String,
    val technicalMessage: String,
    userMessage: String,
    statusCode: Int,
    errorCode: String,
    cause: Throwable? = null
) : BuscaMedException(userMessage, statusCode, errorCode, cause)
