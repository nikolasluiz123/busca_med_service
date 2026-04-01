package br.com.buscamed.api.v1.pillpack

import br.com.buscamed.core.config.exception.configureStatusPages
import br.com.buscamed.core.config.security.AuthConstants
import br.com.buscamed.core.config.serialization.configureSerialization
import br.com.buscamed.core.enumeration.SupportedImageFormat
import br.com.buscamed.domain.model.LLMExecutionHistory
import br.com.buscamed.domain.usecase.DownloadImageUseCase
import br.com.buscamed.domain.usecase.GetLLMExecutionHistoryUseCase
import br.com.buscamed.domain.usecase.ProcessImageUseCase
import br.com.buscamed.domain.usecase.ProcessTextUseCase
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import java.time.Instant

/**
 * Suite de testes para validação do roteamento e integração de [PillPackController].
 */
class PillPackRoutingTest {

    companion object {
        private const val MOCK_TOKEN = "Bearer mock-token"
        private const val VALID_JSON_PAYLOAD = """{"text": "Aspirina"}"""
        private const val MOCK_LLM_RESPONSE = """{"componentes": []}"""

        private val ENDPOINT_PROCESS_TEXT = "${PillPackRoutes.V1_ROOT}${PillPackRoutes.PROCESS_TEXT}"
        private val ENDPOINT_PROCESS_IMAGE = "${PillPackRoutes.V1_ROOT}${PillPackRoutes.PROCESS_IMAGE}"
        private val ENDPOINT_HISTORY = "${PillPackRoutes.V1_ROOT}${PillPackRoutes.HISTORY}"
        private val ENDPOINT_IMAGE = "${PillPackRoutes.V1_ROOT}${PillPackRoutes.IMAGE}"
    }

    private val processImageUseCaseMock = mockk<ProcessImageUseCase>(relaxed = true)
    private val processTextUseCaseMock = mockk<ProcessTextUseCase>(relaxed = true)
    private val getHistoryUseCaseMock = mockk<GetLLMExecutionHistoryUseCase>(relaxed = true)
    private val downloadImageUseCaseMock = mockk<DownloadImageUseCase>(relaxed = true)

    @Test
    fun getHistory_withoutAuthentication_returns401() = testApplication {
        application { setupTestEnvironment(simulateAuthSuccess = false) }

        val response = client.get(ENDPOINT_HISTORY)

        assertEquals(HttpStatusCode.Unauthorized, response.status)
        coVerify(exactly = 0) { getHistoryUseCaseMock.invoke(any()) }
    }

    @Test
    fun getHistory_withAuthentication_returns200() = testApplication {
        application { setupTestEnvironment(simulateAuthSuccess = true) }
        coEvery { getHistoryUseCaseMock.invoke(any()) } returns listOf(
            LLMExecutionHistory(0, 0, MOCK_LLM_RESPONSE, true, Instant.now(), Instant.now())
        )

        val response = client.get(ENDPOINT_HISTORY) {
            header(HttpHeaders.Authorization, MOCK_TOKEN)
        }

        assertEquals(HttpStatusCode.OK, response.status)
        coVerify(exactly = 1) { getHistoryUseCaseMock.invoke(any()) }
    }

    @Test
    fun postProcessImage_malformedMultipart_returns401() = testApplication {
        application { setupTestEnvironment(simulateAuthSuccess = false) }

        val response = client.post(ENDPOINT_PROCESS_IMAGE) {
            contentType(ContentType.Application.Json)
            setBody("""{"image": "invalid-format"}""")
        }

        assertEquals(HttpStatusCode.Unauthorized, response.status)
        coVerify(exactly = 0) { processImageUseCaseMock.invoke(any(), any()) }
    }

    @Test
    fun postProcessImage_withAuthenticationAndValidPayload_returns200() = testApplication {
        application { setupTestEnvironment(simulateAuthSuccess = true) }
        coEvery { processImageUseCaseMock.invoke(any(), any()) } returns MOCK_LLM_RESPONSE

        val response = client.submitFormWithBinaryData(
            url = ENDPOINT_PROCESS_IMAGE,
            formData = formData {
                append("image", byteArrayOf(1, 2, 3), Headers.build {
                    append(HttpHeaders.ContentType, "image/png")
                    append(HttpHeaders.ContentDisposition, "filename=\"test.png\"")
                })
                append("mimeType", "image/png")
            }
        ) {
            header(HttpHeaders.Authorization, MOCK_TOKEN)
        }

        assertEquals(HttpStatusCode.OK, response.status)
        coVerify(exactly = 1) { processImageUseCaseMock.invoke(any(), any()) }
    }

    @Test
    fun postProcessText_withAuthenticationAndValidPayload_returns200() = testApplication {
        application { setupTestEnvironment(simulateAuthSuccess = true) }
        coEvery { processTextUseCaseMock.invoke(any()) } returns MOCK_LLM_RESPONSE

        val response = client.post(ENDPOINT_PROCESS_TEXT) {
            header(HttpHeaders.Authorization, MOCK_TOKEN)
            contentType(ContentType.Application.Json)
            setBody(VALID_JSON_PAYLOAD)
        }

        assertEquals(HttpStatusCode.OK, response.status)
        coVerify(exactly = 1) { processTextUseCaseMock.invoke(any()) }
    }

    @Test
    fun downloadImage_withAuthenticationAndValidId_returns200() = testApplication {
        application { setupTestEnvironment(simulateAuthSuccess = true) }
        coEvery { downloadImageUseCaseMock.invoke(any()) } returns Pair(byteArrayOf(1, 2, 3), SupportedImageFormat.PNG)

        val response = client.get("$ENDPOINT_IMAGE?executionId=123") {
            header(HttpHeaders.Authorization, MOCK_TOKEN)
        }

        assertEquals(HttpStatusCode.OK, response.status)
        coVerify(exactly = 1) { downloadImageUseCaseMock.invoke("123") }
    }

    private fun Application.setupTestEnvironment(simulateAuthSuccess: Boolean) {
        install(Koin) {
            modules(module {
                single { processImageUseCaseMock }
                single { processTextUseCaseMock }
                single { getHistoryUseCaseMock }
                single { downloadImageUseCaseMock }
                single { PillPackController(get(), get(), get(), get()) }
            })
        }
        configureSerialization()
        configureStatusPages()

        install(Authentication) {
            bearer(AuthConstants.AUTH_FIREBASE_NAME) {
                authenticate { if (simulateAuthSuccess) UserIdPrincipal("test-user") else null }
            }
            bearer(AuthConstants.AUTH_GOOGLE_OIDC_NAME) {
                authenticate { if (simulateAuthSuccess) UserIdPrincipal("test-user") else null }
            }
        }

        routing {
            pillPackRoutes()
        }
    }
}