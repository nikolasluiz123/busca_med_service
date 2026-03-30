package br.com.buscamed.core.config.security.exeption

/**
 * Objeto utilitário que centraliza as constantes de códigos de erro do sistema.
 * 
 * Estes códigos são retornados no corpo das respostas HTTP para permitir
 * que os clientes (frontend, outros serviços) identifiquem e tratem falhas
 * de forma programática.
 */
object ServiceErrorCodes {
    /** Erro genérico para falhas inesperadas (Status 500). */
    const val INTERNAL_ERROR = "INTERNAL_SERVER_ERROR"
    
    /** O serviço ou uma de suas dependências está indisponível (Status 503/502). */
    const val SERVICE_UNAVAILABLE = "SERVICE_UNAVAILABLE"

    /** Uma regra de negócio da aplicação foi violada (Status 400). */
    const val BUSINESS_ERROR = "BUSINESS_RULE_ERROR"
    
    /** O usuário não possui as permissões necessárias (Status 401/403). */
    const val ACCESS_DENIED = "ACCESS_DENIED"
    
    /** O recurso solicitado não existe (Status 404). */
    const val RESOURCE_NOT_FOUND = "RESOURCE_NOT_FOUND"

    /** Falha na configuração ou obtenção de credenciais GCP (Status 500). */
    const val GCP_AUTH_FAILURE = "GCP_AUTH_CONFIGURATION_ERROR"
}
