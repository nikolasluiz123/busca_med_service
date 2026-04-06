package br.com.buscamed.api.v1.anvisa

import br.com.buscamed.core.config.security.AuthConstants
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import org.koin.ktor.ext.inject

/**
 * Configura os endpoints para o contexto de integração com a ANVISA.
 */
fun Route.anvisaRoutes() {
    val controller: AnvisaController by inject()

    route(AnvisaRoutes.V1_ROOT) {
        authenticate(AuthConstants.AUTH_GOOGLE_OIDC_NAME) {
            post(AnvisaRoutes.IMPORT_MEDICATIONS) {
                controller.importInformation(call)
            }

            post(AnvisaRoutes.IMPORT_LEAFLET) {
                controller.importLeaflets(call)
            }
        }
    }
}