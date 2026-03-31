package br.com.buscamed.data.client.anvisa

import br.com.buscamed.core.config.serialization.DefaultJson
import br.com.buscamed.data.client.anvisa.dto.AnvisaDatasetResponseDTO
import br.com.buscamed.data.client.anvisa.dto.AnvisaResourceDTO
import br.com.buscamed.data.client.anvisa.exception.AnvisaIntegrationException
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

/**
 * Unit tests for [AnvisaIntegrationKtorClient].
 */
class AnvisaIntegrationKtorClientTest {

    companion object {
        private const val ANVISA_DATASET_API_URL = "https://dados.gov.br/api/publico/conjuntos-dados/preco-de-medicamentos-no-brasil-consumidor"
        private const val FAKE_CSV_DOWNLOAD_URL = "http://fake.url/file.csv"
        private const val FAKE_PDF_DOWNLOAD_URL = "http://fake.url/file.pdf"
        private const val FORMAT_CSV = "CSV"
        private const val FORMAT_PDF = "PDF"
        private const val EXPECTED_MISSING_CSV_ERROR_MESSAGE = "recurso no formato CSV não foi encontrado"
        private const val EXPECTED_DOWNLOAD_FAILURE_MESSAGE = "Falha ao realizar o download"
        private const val MOCK_CSV_CONTENT = "col1;col2\nval1;val2"
    }

    private fun createMockClient(engine: MockEngine): HttpClient {
        return HttpClient(engine) {
            install(ContentNegotiation) {
                json(DefaultJson)
            }
        }
    }

    @Test
    fun downloadPricesCsv_metadataResponseFails_throwsExceptionWithResponseStatusCode() = runBlocking {
        val mockEngine = MockEngine {
            respondError(HttpStatusCode.BadGateway)
        }
        val client = AnvisaIntegrationKtorClient(createMockClient(mockEngine))

        val exception = assertThrows<AnvisaIntegrationException> {
            client.downloadPricesCsv()
        }

        assertEquals(502, exception.statusCode)
    }

    @Test
    fun downloadPricesCsv_csvResourceMissing_throwsExceptionWith404() = runBlocking {
        val mockEngine = MockEngine {
            val mockMetadata = AnvisaDatasetResponseDTO(
                resources = listOf(
                    AnvisaResourceDTO(format = FORMAT_PDF, url = FAKE_PDF_DOWNLOAD_URL)
                )
            )
            respond(
                content = DefaultJson.encodeToString(AnvisaDatasetResponseDTO.serializer(), mockMetadata),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        val client = AnvisaIntegrationKtorClient(createMockClient(mockEngine))

        val exception = assertThrows<AnvisaIntegrationException> {
            client.downloadPricesCsv()
        }

        assertEquals(404, exception.statusCode)
        assertTrue(exception.technicalMessage.contains(EXPECTED_MISSING_CSV_ERROR_MESSAGE))
    }

    @Test
    fun downloadPricesCsv_csvUrlNotFoundDuringDownload_throwsExceptionWith404() = runBlocking {
        val mockEngine = MockEngine { request ->
            when (request.url.toString()) {
                ANVISA_DATASET_API_URL -> {
                    val mockMetadata = AnvisaDatasetResponseDTO(
                        resources = listOf(
                            AnvisaResourceDTO(format = FORMAT_CSV, url = FAKE_CSV_DOWNLOAD_URL)
                        )
                    )
                    respond(
                        content = DefaultJson.encodeToString(AnvisaDatasetResponseDTO.serializer(), mockMetadata),
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                }
                FAKE_CSV_DOWNLOAD_URL -> {
                    respondError(HttpStatusCode.NotFound)
                }
                else -> respondError(HttpStatusCode.BadRequest)
            }
        }
        val client = AnvisaIntegrationKtorClient(createMockClient(mockEngine))

        val exception = assertThrows<AnvisaIntegrationException> {
            client.downloadPricesCsv()
        }

        assertEquals(404, exception.statusCode)
        assertTrue(exception.technicalMessage.contains(EXPECTED_DOWNLOAD_FAILURE_MESSAGE))
    }

    @Test
    fun downloadPricesCsv_downloadExecuteFails_throwsExceptionWithResponseStatusCode() = runBlocking {
        val mockEngine = MockEngine { request ->
            when (request.url.toString()) {
                ANVISA_DATASET_API_URL -> {
                    val mockMetadata = AnvisaDatasetResponseDTO(
                        resources = listOf(
                            AnvisaResourceDTO(format = FORMAT_CSV, url = FAKE_CSV_DOWNLOAD_URL)
                        )
                    )
                    respond(
                        content = DefaultJson.encodeToString(AnvisaDatasetResponseDTO.serializer(), mockMetadata),
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                }
                FAKE_CSV_DOWNLOAD_URL -> {
                    respondError(HttpStatusCode.InternalServerError)
                }
                else -> respondError(HttpStatusCode.BadRequest)
            }
        }
        val client = AnvisaIntegrationKtorClient(createMockClient(mockEngine))

        val exception = assertThrows<AnvisaIntegrationException> {
            client.downloadPricesCsv()
        }

        assertEquals(500, exception.statusCode)
        assertTrue(exception.technicalMessage.contains(EXPECTED_DOWNLOAD_FAILURE_MESSAGE))
    }

    @Test
    fun downloadPricesCsv_validMetadata_transformsBodyToDtoAndReturnsCsvBytes() = runBlocking {
        val expectedCsvContentBytes = MOCK_CSV_CONTENT.toByteArray()
        val mockEngine = MockEngine { request ->
            when (request.url.toString()) {
                ANVISA_DATASET_API_URL -> {
                    val jsonBody = """
                        {
                            "resources": [
                                { "format": "$FORMAT_PDF", "url": "$FAKE_PDF_DOWNLOAD_URL" },
                                { "format": "$FORMAT_CSV", "url": "$FAKE_CSV_DOWNLOAD_URL" }
                            ]
                        }
                    """.trimIndent()

                    respond(
                        content = jsonBody,
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                }
                FAKE_CSV_DOWNLOAD_URL -> {
                    respond(
                        content = expectedCsvContentBytes,
                        status = HttpStatusCode.OK
                    )
                }
                else -> respondError(HttpStatusCode.NotFound)
            }
        }

        val client = AnvisaIntegrationKtorClient(createMockClient(mockEngine))

        val result = client.downloadPricesCsv()

        assertArrayEquals(expectedCsvContentBytes, result)
    }
}