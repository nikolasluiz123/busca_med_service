package br.com.buscamed.core.config

import br.com.buscamed.api.v1.anvisa.AnvisaController
import br.com.buscamed.api.v1.pillpack.PillPackController
import br.com.buscamed.api.v1.prescription.PrescriptionController
import br.com.buscamed.core.config.security.AuthConstants
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.testing.*
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin

/**
 * Suite de testes para validação das rotas base da aplicação.
 */
class RootRoutingTest {

    companion object {
        private const val ROOT_PATH = "/"
        private const val EXPECTED_ONLINE_MESSAGE = "BuscaMed Service: Online"
    }

    @Test
    fun getRoot_requestingRootPath_returns200AndOnlineStatusMessage() = testApplication {
        setupTestEnvironment()

        val response = client.get(ROOT_PATH)

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(EXPECTED_ONLINE_MESSAGE, response.bodyAsText())
    }

    private fun ApplicationTestBuilder.setupTestEnvironment() {
        application {
            install(Koin) {
                modules(module {
                    single { mockk<PrescriptionController>(relaxed = true) }
                    single { mockk<PillPackController>(relaxed = true) }
                    single { mockk<AnvisaController>(relaxed = true) }
                })
            }

            install(Authentication) {
                bearer(AuthConstants.AUTH_FIREBASE_NAME) {
                    authenticate { null }
                }
                bearer(AuthConstants.AUTH_GOOGLE_OIDC_NAME) {
                    authenticate { null }
                }
            }

            configureRouting()
        }
    }
}