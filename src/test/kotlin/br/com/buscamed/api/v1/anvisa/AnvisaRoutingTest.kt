package br.com.buscamed.api.v1.anvisa

import br.com.buscamed.core.config.exception.configureStatusPages
import br.com.buscamed.core.config.security.AuthConstants
import br.com.buscamed.core.config.serialization.configureSerialization
import br.com.buscamed.domain.parser.AnvisaCsvParseResult
import br.com.buscamed.domain.parser.AnvisaCsvParser
import br.com.buscamed.domain.repository.AnvisaMedicationRepository
import br.com.buscamed.domain.repository.SystemProcessControlRepository
import br.com.buscamed.domain.service.AnvisaIntegrationService
import br.com.buscamed.domain.service.CsvStorageService
import br.com.buscamed.domain.service.LeafletStorageService
import br.com.buscamed.domain.usecase.ImportAnvisaInformationUseCase
import br.com.buscamed.domain.usecase.ImportAnvisaLeafletsUseCase
import io.ktor.client.request.*
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

/**
 * Suite de testes para validação do roteamento e integração de [AnvisaController].
 */
class AnvisaRoutingTest {

    companion object {
        private const val MOCK_TOKEN = "Bearer mock-token-valido"
        private const val ENDPOINT_IMPORT = "${AnvisaRoutes.V1_ROOT}${AnvisaRoutes.IMPORT_MEDICATIONS}"
    }

    private val integrationServiceMock = mockk<AnvisaIntegrationService>(relaxed = true)
    private val csvParserMock = mockk<AnvisaCsvParser>(relaxed = true)
    private val medicationRepositoryMock = mockk<AnvisaMedicationRepository>(relaxed = true)
    private val csvStorageService = mockk<CsvStorageService>(relaxed = true)
    private val leafletStorageService = mockk<LeafletStorageService>(relaxed = true)
    private val processControlRepositoryMock = mockk<SystemProcessControlRepository>(relaxed = true)

    private val importAnvisaInformationUseCase = ImportAnvisaInformationUseCase(
        integrationService = integrationServiceMock,
        csvParser = csvParserMock,
        medicationRepository = medicationRepositoryMock,
        storageService = csvStorageService,
        processControlRepository = processControlRepositoryMock
    )

    private val importAnvisaLeafletsUseCase = ImportAnvisaLeafletsUseCase(
        repository = medicationRepositoryMock,
        anvisaService = integrationServiceMock,
        storageService = leafletStorageService,
        processControlRepository = processControlRepositoryMock
    )
    @Test
    fun postImport_withoutOidcAuthentication_returns401() = testApplication {
        application { setupTestEnvironment(simulateAuthSuccess = false) }

        val response = client.post(ENDPOINT_IMPORT)

        assertEquals(HttpStatusCode.Unauthorized, response.status)
        coVerify(exactly = 0) { integrationServiceMock.downloadPricesCsv() }
    }

    @Test
    fun postImport_withOidcAuthentication_returns202() = testApplication {
        application { setupTestEnvironment(simulateAuthSuccess = true) }

        val expectedParseResult = AnvisaCsvParseResult(
            medications = emptyList(),
            cleanedCsvBytes = ByteArray(0)
        )

        coEvery { csvParserMock.parse(any()) } returns expectedParseResult
        coEvery { integrationServiceMock.downloadPricesCsv() } returns ByteArray(0)

        val response = client.post(ENDPOINT_IMPORT) {
            header(HttpHeaders.Authorization, MOCK_TOKEN)
        }

        assertEquals(HttpStatusCode.Accepted, response.status)
        coVerify(exactly = 1) { integrationServiceMock.downloadPricesCsv() }
    }

    @Test
    fun postImport_integrationFailure_returns500() = testApplication {
        application { setupTestEnvironment(simulateAuthSuccess = true) }

        coEvery { integrationServiceMock.downloadPricesCsv() } throws RuntimeException("Connection Error")

        val response = client.post(ENDPOINT_IMPORT) {
            header(HttpHeaders.Authorization, MOCK_TOKEN)
        }

        assertEquals(HttpStatusCode.InternalServerError, response.status)
        coVerify(exactly = 1) { integrationServiceMock.downloadPricesCsv() }
        coVerify(exactly = 0) { csvParserMock.parse(any()) }
    }

    private fun Application.setupTestEnvironment(simulateAuthSuccess: Boolean) {
        install(Koin) {
            modules(module {
                single { importAnvisaInformationUseCase }
                single { importAnvisaLeafletsUseCase }
                single {
                    AnvisaController(
                        importAnvisaInformationUseCase = get(),
                        importAnvisaLeafletsUseCase = get()
                    )
                }
            })
        }
        configureSerialization()
        configureStatusPages()

        install(Authentication) {
            bearer(AuthConstants.AUTH_GOOGLE_OIDC_NAME) {
                authenticate {
                    if (simulateAuthSuccess) UserIdPrincipal("service-account") else null
                }
            }
        }

        routing {
            anvisaRoutes()
        }
    }
}