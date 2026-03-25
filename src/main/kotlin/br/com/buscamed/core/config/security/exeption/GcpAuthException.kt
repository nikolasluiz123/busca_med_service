package br.com.buscamed.core.config.security.exeption

import br.com.buscamed.domain.exceptions.BuscaMedException
import io.ktor.http.*

/**
 * Exceção lançada quando há falhas na obtenção de credenciais ou tokens do GCP.
 */
open class GcpAuthException(
    val technicalMessage: String,
    cause: Throwable? = null
): BuscaMedException(
    userMessage = "Erro interno de configuração de segurança.",
    httpStatusCode = HttpStatusCode.InternalServerError,
    errorCode = ServiceErrorCodes.GCP_AUTH_FAILURE,
    cause = cause
) {

    /**
     * Indica que as credenciais carregadas no ambiente não suportam a operação desejada (ex: OIDC).
     * Isso geralmente ocorre em ambiente local sem a configuração correta de Impersonation.
     */
    class InvalidCredentialsType(
        currentType: String,
        details: String
    ) : GcpAuthException("Tipo de credencial inválido ($currentType): $details")
}