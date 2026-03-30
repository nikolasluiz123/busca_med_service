package br.com.buscamed.core.config.security

/**
 * Objeto utilitário que centraliza as constantes utilizadas nas configurações e
 * validações de autenticação e autorização (Security).
 */
object AuthConstants {
    /** Nome de identificação do provedor de autenticação via Firebase (usuários finais). */
    const val AUTH_FIREBASE_NAME = "auth-jwt-firebase"
    /** Nome de identificação do provedor de autenticação via Google OIDC (service-to-service). */
    const val AUTH_GOOGLE_OIDC_NAME = "auth-google-oidc"
    
    /** URL padrão para buscar as chaves públicas (JWKS) do Firebase Auth. */
    const val URL_JWKS_FIREBASE = "https://www.googleapis.com/service_accounts/v1/jwk/securetoken@system.gserviceaccount.com"
    /** URL padrão para buscar as chaves públicas (JWKS) do Google OIDC. */
    const val URL_JWKS_GOOGLE_OIDC = "https://www.googleapis.com/oauth2/v3/certs"
    
    /** Emissor (Issuer) esperado para tokens gerados pelo Google (OIDC). */
    const val ISSUER_GOOGLE_ACCOUNTS = "https://accounts.google.com"
    /** Prefixo do emissor (Issuer) esperado para tokens gerados pelo Firebase Auth. O Project ID é anexado ao final. */
    const val ISSUER_PREFIX_FIREBASE = "https://securetoken.google.com/"
    
    /** O realm de autenticação enviado em respostas de desafio (ex: HTTP 401). */
    const val REALM = "Buscamed Services"
}
