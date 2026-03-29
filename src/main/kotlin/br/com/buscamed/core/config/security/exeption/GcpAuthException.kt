package br.com.buscamed.core.config.security.exeption

import br.com.buscamed.domain.exceptions.BuscaMedException

open class GcpAuthException(
    val technicalMessage: String,
    cause: Throwable? = null
): BuscaMedException(
    userMessage = "Erro interno de configuração de segurança.",
    statusCode = 500,
    errorCode = ServiceErrorCodes.GCP_AUTH_FAILURE,
    cause = cause
) {
    class InvalidCredentialsType(
        currentType: String,
        details: String
    ) : GcpAuthException("Tipo de credencial inválido ($currentType): $details")
}