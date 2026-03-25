package br.com.buscamed.core.config.monitoring

import io.ktor.server.application.*
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.request.header
import io.ktor.server.request.host
import io.ktor.server.request.httpMethod
import io.ktor.server.request.path
import io.ktor.server.request.userAgent
import io.ktor.util.AttributeKey
import org.slf4j.event.Level

/**
 * Chave privada para armazenar o tempo de início da requisição.
 * Usamos AttributeKey para garantir segurança de tipo e evitar colisões de strings.
 */
private val RequestStartTimeKey = AttributeKey<Long>("RequestStartTime")

fun Application.configureMonitoring(
    config: MonitoringConfig = MonitoringConfig()
) {
    intercept(ApplicationCallPipeline.Monitoring) {
        call.attributes.put(RequestStartTimeKey, System.currentTimeMillis())
        proceed()
    }

    install(CallLogging) {
        level = Level.INFO

        filter { call ->
            val path = call.request.path()
            config.ignoredPaths.none { ignored -> path.startsWith(ignored) }
        }

        mdc("gcp_trace_id") { call ->
            call.request.header("X-Cloud-Trace-Context")?.split("/")?.firstOrNull()
        }

        mdc("http_method") { call -> call.request.httpMethod.value }
        mdc("http_host") { call -> call.request.host() }
        mdc("http_path") { call -> call.request.path() }
        mdc("user_agent") { call -> call.request.userAgent() }

        if (config.includeUserPrincipal) {
            mdc("user_id") { call ->
                val principal = call.principal<JWTPrincipal>()
                principal?.payload?.subject ?: principal?.payload?.getClaim("uid")?.asString()
            }
        }

        format { call ->
            val status = call.response.status()
            val timing = call.processingTimeMillis()
            "HTTP ${call.request.httpMethod.value} ${call.request.path()} -> $status (${timing}ms)"
        }
    }
}

/**
 * Calcula o tempo decorrido desde o início da requisição até o momento do log.
 */
private fun ApplicationCall.processingTimeMillis(): Long {
    val startTime = this.attributes.getOrNull(RequestStartTimeKey) ?: System.currentTimeMillis()
    return System.currentTimeMillis() - startTime
}