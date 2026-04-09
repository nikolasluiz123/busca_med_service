package br.com.buscamed.api.v1.leaflet

import br.com.buscamed.core.config.security.AuthConstants
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import org.koin.ktor.ext.inject

/**
 * Configura os endpoints e regras de roteamento para o contexto de Bulas (Leaflet).
 */
fun Route.leafletRoutes() {
    val controller: LeafletController by inject()

    route(LeafletRoutes.V1_ROOT) {
        authenticate(AuthConstants.AUTH_GOOGLE_OIDC_NAME) {
            post(LeafletRoutes.RESUME) {
                controller.resume(call)
            }
        }

        authenticate(AuthConstants.AUTH_FIREBASE_NAME) {
            post(LeafletRoutes.RESUME_PATIENT) {
                controller.resumePatient(call)
            }

            post(LeafletRoutes.RESUME_PROFESSIONAL) {
                controller.resumeProfessional(call)
            }
        }
    }
}