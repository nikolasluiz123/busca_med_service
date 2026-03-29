package br.com.buscamed.core.config.exception

import br.com.buscamed.core.config.security.exeption.ServiceErrorCodes
import br.com.buscamed.core.dto.ErrorResponseDTO
import br.com.buscamed.data.client.core.exception.IntegrationException
import br.com.buscamed.domain.exceptions.BuscaMedException
import io.ktor.http.HttpStatusCode
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.application.*
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.path
import org.slf4j.LoggerFactory

fun Application.configureStatusPages() {
    val logger = LoggerFactory.getLogger("GlobalExceptionHandler")

    install(StatusPages) {
        exception<BuscaMedException> { call, cause ->
            val traceId = call.response.headers["X-Cloud-Trace-Context"]
            val ktorStatusCode = HttpStatusCode.fromValue(cause.statusCode)

            if (cause.statusCode >= 500) {
                logger.error("Erro de Sistema [${cause.errorCode}]: ${cause.message}", cause)

                if (cause is IntegrationException) {
                    logger.error("Detalhes técnicos da integração: ${cause.serviceName} -> ${cause.technicalMessage}")
                }
            } else {
                logger.warn("Erro de Negócio [${cause.errorCode}]: ${cause.message} (User: ${call.extractUserLogInfo()})")
            }

            call.respond(
                ktorStatusCode,
                ErrorResponseDTO(
                    errorCode = cause.errorCode,
                    message = cause.userMessage,
                    path = call.request.path(),
                    traceId = traceId
                )
            )
        }

        exception<Throwable> { call, cause ->
            val traceId = call.response.headers["X-Cloud-Trace-Context"]

            logger.error("ERRO CRÍTICO NÃO TRATADO: ${cause.message}", cause)

            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponseDTO(
                    errorCode = ServiceErrorCodes.INTERNAL_ERROR,
                    message = "Ocorreu um erro interno inesperado. Tente novamente mais tarde.",
                    path = call.request.path(),
                    traceId = traceId
                )
            )
        }
    }
}

private fun ApplicationCall.extractUserLogInfo(): String {
    val principal = this.principal<JWTPrincipal>()
    return principal?.payload?.subject ?: principal?.payload?.getClaim("uid")?.asString() ?: "Anonymous"
}