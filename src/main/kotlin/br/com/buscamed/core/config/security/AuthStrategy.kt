package br.com.buscamed.core.config.security

import io.ktor.server.auth.jwt.*

/**
 * Sealed Class que define as estratégias de autenticação suportadas.
 * O serviço DEVE instanciar uma dessas classes passando os dados necessários.
 */
sealed class AuthStrategy(
    val authName: String,
    val defaultJwksUrl: String,
    open val jwkConfig: JwkProviderConfig
) {
    abstract fun getIssuer(): String
    
    abstract fun getAudience(): String
    
    open fun validate(credential: JWTCredential): JWTPrincipal? {
        return if (credential.payload.audience.contains(getAudience())) {
            JWTPrincipal(credential.payload)
        } else {
            null
        }
    }

    /**
     * Estratégia para Usuários Finais (Firebase Auth).
     * Exige o Project ID.
     */
    data class FirebaseAuth(
        val projectId: String,
        override val jwkConfig: JwkProviderConfig
    ) : AuthStrategy(
        authName = AuthConstants.AUTH_FIREBASE_NAME,
        defaultJwksUrl = AuthConstants.URL_JWKS_FIREBASE,
        jwkConfig = jwkConfig
    ) {
        override fun getIssuer() = "${AuthConstants.ISSUER_PREFIX_FIREBASE}$projectId"
        override fun getAudience() = projectId
    }

    /**
     * Estratégia para Service-to-Service / Admin (Google OIDC).
     * Exige a definição da Audiência esperada (geralmente a URL do serviço ou Client ID).
     */
    data class GoogleOidc(
        val targetAudience: String,
        override val jwkConfig: JwkProviderConfig
    ) : AuthStrategy(
        authName = AuthConstants.AUTH_GOOGLE_OIDC_NAME,
        defaultJwksUrl = AuthConstants.URL_JWKS_GOOGLE_OIDC,
        jwkConfig = jwkConfig
    ) {
        override fun getIssuer() = AuthConstants.ISSUER_GOOGLE_ACCOUNTS
        override fun getAudience() = targetAudience
    }
}