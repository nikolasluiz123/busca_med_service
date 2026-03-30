package br.com.buscamed.core.config.security.exeption

import br.com.buscamed.domain.exceptions.BuscaMedException

/**
 * Exceção base lançada quando ocorre um erro na configuração ou obtenção de
 * credenciais para serviços do Google Cloud Platform (GCP).
 *
 * @param technicalMessage Detalhes técnicos sobre a falha, úteis para depuração.
 * @param cause Exceção original que causou a falha, se existir.
 */
open class GcpAuthException(
    val technicalMessage: String,
    cause: Throwable? = null
): BuscaMedException(
    userMessage = "Erro interno de configuração de segurança.",
    statusCode = 500,
    errorCode = ServiceErrorCodes.GCP_AUTH_FAILURE,
    cause = cause
) {
    /**
     * Lançada quando o tipo de credencial recuperada não suporta a operação
     * desejada (por exemplo, quando se espera um `IdTokenProvider` e se obtém outro tipo).
     *
     * @param currentType O tipo da classe da credencial inválida recuperada.
     * @param details Explicação adicional sobre o problema e como resolvê-lo.
     */
    class InvalidCredentialsType(
        currentType: String,
        details: String
    ) : GcpAuthException("Tipo de credencial inválido ($currentType): $details")
}
