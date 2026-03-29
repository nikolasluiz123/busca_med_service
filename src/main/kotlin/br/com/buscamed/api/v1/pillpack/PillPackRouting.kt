package br.com.buscamed.api.v1.pillpack

import br.com.buscamed.core.config.security.AuthConstants
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

/**
 * Configura os endpoints para o contexto de Cartelas de Comprimidos.
 */
fun Route.pillPackRoutes() {
    val controller: PillPackController by inject()

    route(PillPackRoutes.V1_ROOT) {
        authenticate(AuthConstants.AUTH_FIREBASE_NAME) {
            post(PillPackRoutes.PROCESS_IMAGE) {
                controller.processImage(call)
            }

            post(PillPackRoutes.PROCESS_TEXT) {
                controller.processText(call)
            }
        }

        authenticate(AuthConstants.AUTH_GOOGLE_OIDC_NAME) {
            get(PillPackRoutes.HISTORY) {
                controller.getHistory(call)
            }

            get(PillPackRoutes.IMAGE) {
                controller.downloadImage(call)
            }
        }
    }
}