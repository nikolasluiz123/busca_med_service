package br.com.buscamed.core.config.security

object AuthConstants {
    const val AUTH_FIREBASE_NAME = "auth-jwt-firebase"
    const val AUTH_GOOGLE_OIDC_NAME = "auth-google-oidc"
    
    const val URL_JWKS_FIREBASE = "https://www.googleapis.com/service_accounts/v1/jwk/securetoken@system.gserviceaccount.com"
    const val URL_JWKS_GOOGLE_OIDC = "https://www.googleapis.com/oauth2/v3/certs"
    
    const val ISSUER_GOOGLE_ACCOUNTS = "https://accounts.google.com"
    const val ISSUER_PREFIX_FIREBASE = "https://securetoken.google.com/"
    
    const val REALM = "Buscamed Services"
}