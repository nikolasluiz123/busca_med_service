package br.com.buscamed

import br.com.buscamed.core.config.configureCORS
import br.com.buscamed.core.config.configureDI
import br.com.buscamed.core.config.configureRouting
import br.com.buscamed.core.config.exception.configureStatusPages
import br.com.buscamed.core.config.monitoring.configureMonitoring
import br.com.buscamed.core.config.security.configureSecurity
import br.com.buscamed.core.config.serialization.configureSerialization
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.module() {
    install(IgnoreTrailingSlash)

    configureDI()

    configureSerialization()
    configureSecurity()
    configureMonitoring()
    configureStatusPages()
    configureCORS()
    configureRouting()

    log.info("Aplicação inicializada com sucesso.")
}