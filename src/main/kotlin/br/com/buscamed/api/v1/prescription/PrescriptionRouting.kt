package br.com.buscamed.api.v1.prescription

import br.com.buscamed.core.config.security.AuthConstants
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import org.koin.ktor.ext.inject

/**
 * Configura os endpoints para o contexto de Prescrição Médica.
 */
fun Route.prescriptionRoutes() {
    val controller: PrescriptionController by inject()

    route(PrescriptionRoutes.V1_ROOT) {
        authenticate(AuthConstants.AUTH_FIREBASE_NAME) {
            post(PrescriptionRoutes.PROCESS_IMAGE) {
                controller.processImage(call)
            }

            post(PrescriptionRoutes.PROCESS_TEXT) {
                controller.processText(call)
            }

        }

        authenticate(AuthConstants.AUTH_GOOGLE_OIDC_NAME) {
            get(PrescriptionRoutes.HISTORY) {
                controller.getHistory(call)
            }

            get(PrescriptionRoutes.IMAGE) {
                controller.downloadImage(call)
            }
        }
    }
}