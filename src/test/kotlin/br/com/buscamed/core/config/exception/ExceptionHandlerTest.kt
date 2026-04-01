package br.com.buscamed.core.config.exception

import br.com.buscamed.core.config.security.exeption.ServiceErrorCodes
import br.com.buscamed.core.config.serialization.configureSerialization
import br.com.buscamed.core.dto.ErrorResponseDTO
import br.com.buscamed.data.client.core.exception.IntegrationException
import br.com.buscamed.domain.exceptions.BusinessException
import br.com.buscamed.domain.exceptions.ResourceNotFoundException
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

/**
 * Suite de testes para validação do [ExceptionHandler] e do plugin StatusPages.
 */
class ExceptionHandlerTest {

    companion object {
        private const val PATH_BUSINESS = "/test-business"
        private const val PATH_NOT_FOUND = "/test-not-found"
        private const val PATH_INTEGRATION = "/test-integration"
        private const val PATH_INTERNAL = "/test-internal"

        private const val ERROR_BUSINESS_MSG = "Regra de negócio violada."
        private const val ERROR_NOT_FOUND_MSG = "Recurso inexistente."
        private const val ERROR_INTEGRATION_USER_MSG = "Serviço indisponível no momento."
        private const val ERROR_INTEGRATION_TECH_MSG = "Connection timeout"
        private const val ERROR_INTEGRATION_CODE = "MOCK_INTEGRATION_ERROR"
        private const val ERROR_INTERNAL_MSG = "Ocorreu um erro interno inesperado. Tente novamente mais tarde."
    }

    @Test
    fun handle_businessException_returnsStatus400AndMappedErrorResponseDTO() = testApplication {
        setupTestEnvironment()

        val response = client.get(PATH_BUSINESS)
        val errorDto = decodeErrorResponse(response.bodyAsText())

        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals(ServiceErrorCodes.BUSINESS_ERROR, errorDto.errorCode)
        assertEquals(ERROR_BUSINESS_MSG, errorDto.message)
        assertEquals(PATH_BUSINESS, errorDto.path)
        assertNotNull(errorDto.timestamp)
    }

    @Test
    fun handle_resourceNotFoundException_returnsStatus404AndMappedErrorResponseDTO() = testApplication {
        setupTestEnvironment()

        val response = client.get(PATH_NOT_FOUND)
        val errorDto = decodeErrorResponse(response.bodyAsText())

        assertEquals(HttpStatusCode.NotFound, response.status)
        assertEquals(ServiceErrorCodes.RESOURCE_NOT_FOUND, errorDto.errorCode)
        assertEquals(ERROR_NOT_FOUND_MSG, errorDto.message)
    }

    @Test
    fun handle_integrationException_returnsCorrectStatusAndMappedErrorResponseDTO() = testApplication {
        setupTestEnvironment()

        val response = client.get(PATH_INTEGRATION)
        val errorDto = decodeErrorResponse(response.bodyAsText())

        assertEquals(HttpStatusCode.BadGateway, response.status)
        assertEquals(ERROR_INTEGRATION_CODE, errorDto.errorCode)
        assertEquals(ERROR_INTEGRATION_USER_MSG, errorDto.message)
    }

    @Test
    fun handle_unexpectedThrowable_returnsStatus500AndGenericErrorResponseDTO() = testApplication {
        setupTestEnvironment()

        val response = client.get(PATH_INTERNAL)
        val errorDto = decodeErrorResponse(response.bodyAsText())

        assertEquals(HttpStatusCode.InternalServerError, response.status)
        assertEquals(ServiceErrorCodes.INTERNAL_ERROR, errorDto.errorCode)
        assertEquals(ERROR_INTERNAL_MSG, errorDto.message)
    }

    private fun ApplicationTestBuilder.setupTestEnvironment() {
        application {
            configureSerialization()
            configureStatusPages()
            routing {
                get(PATH_BUSINESS) { throw BusinessException(ERROR_BUSINESS_MSG) }
                get(PATH_NOT_FOUND) { throw ResourceNotFoundException(ERROR_NOT_FOUND_MSG) }
                get(PATH_INTERNAL) { throw NullPointerException("Unexpected null reference") }
                get(PATH_INTEGRATION) {
                    throw IntegrationException(
                        serviceName = "MockService",
                        technicalMessage = ERROR_INTEGRATION_TECH_MSG,
                        userMessage = ERROR_INTEGRATION_USER_MSG,
                        statusCode = HttpStatusCode.BadGateway.value,
                        errorCode = ERROR_INTEGRATION_CODE
                    )
                }
            }
        }
    }

    private fun decodeErrorResponse(body: String): ErrorResponseDTO {
        return Json.decodeFromString<ErrorResponseDTO>(body)
    }
}