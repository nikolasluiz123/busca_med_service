package br.com.buscamed.core.config.monitoring

/**
 * Configuração para definir o comportamento dos logs de requisição.
 */
data class MonitoringConfig(
    /**
     * Lista de caminhos que NÃO devem gerar logs de acesso.
     * Útil para endpoints de health check chamados a cada segundo pelo K8s/Cloud Run.
     */
    val ignoredPaths: List<String> = listOf("/health", "/liveness", "/readiness", "/metrics"),
    
    /**
     * Define se deve tentar extrair informações do Principal (usuário logado).
     */
    val includeUserPrincipal: Boolean = true
)