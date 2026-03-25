package br.com.buscamed.core.config.security.exeption

object ServiceErrorCodes {
    const val INTERNAL_ERROR = "INTERNAL_SERVER_ERROR"
    const val SERVICE_UNAVAILABLE = "SERVICE_UNAVAILABLE"

    const val BUSINESS_ERROR = "BUSINESS_RULE_ERROR"
    const val ACCESS_DENIED = "ACCESS_DENIED"
    const val RESOURCE_NOT_FOUND = "RESOURCE_NOT_FOUND"

    const val GCP_AUTH_FAILURE = "GCP_AUTH_CONFIGURATION_ERROR"
}