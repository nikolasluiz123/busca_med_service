package br.com.buscamed.core.config.security

import com.google.auth.oauth2.GoogleCredentials
import com.google.auth.oauth2.IdTokenProvider
import br.com.buscamed.core.config.security.exeption.GcpAuthException

object GcpServiceAuth {

    /**
     * Gera um ID Token OIDC para autenticação entre serviços.
     * * @param audience A URL do serviço destino.
     * @return O token JWT em formato String.
     * @throws br.com.buscamed.core.config.security.exeption.GcpAuthException.InvalidCredentialsType Se a credencial do ambiente não suportar tokens ID.
     * @throws java.io.IOException Se houver erro de rede ao contactar o servidor de metadados.
     */
    fun getOidcToken(audience: String): String {
        val credentials = GoogleCredentials.getApplicationDefault()

        if (credentials !is IdTokenProvider) {
            throw GcpAuthException.InvalidCredentialsType(
                currentType = credentials::class.java.simpleName,
                details = "A credencial atual não implementa IdTokenProvider. Verifique se você está usando uma Service Account ou se configurou impersonation."
            )
        }

        return credentials.idTokenWithAudience(audience, emptyList()).tokenValue
    }
}