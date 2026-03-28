package br.com.buscamed.core.config

import br.com.buscamed.api.v1.anvisa.anvisaRoutes
import br.com.buscamed.api.v1.pillpack.pillPackRoutes
import br.com.buscamed.api.v1.prescription.prescriptionRoutes
import io.ktor.server.application.*
import io.ktor.server.plugins.openapi.*
import io.ktor.server.plugins.swagger.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("BuscaMed Service: Online")
        }

        swaggerUI(path = "swagger", swaggerFile = "openapi/documentation.yaml")
        openAPI(path = "openapi", swaggerFile = "openapi/documentation.yaml")

        prescriptionRoutes()
        pillPackRoutes()
        anvisaRoutes()
    }
}