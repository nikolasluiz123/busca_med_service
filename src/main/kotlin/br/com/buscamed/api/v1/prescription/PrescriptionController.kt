package br.com.buscamed.api.v1.prescription

import br.com.buscamed.api.v1.dto.request.TextRequestDTO
import br.com.buscamed.api.v1.dto.response.PrescriptionResponseDTO
import br.com.buscamed.api.v1.extensions.extractImageMultipart
import br.com.buscamed.api.v1.mapper.toDTO
import br.com.buscamed.core.config.serialization.DefaultJson
import br.com.buscamed.domain.exceptions.BusinessException
import br.com.buscamed.domain.exceptions.ResourceNotFoundException
import br.com.buscamed.domain.usecase.DownloadImageUseCase
import br.com.buscamed.domain.usecase.GetLLMExecutionHistoryUseCase
import br.com.buscamed.domain.usecase.ProcessImageUseCase
import br.com.buscamed.domain.usecase.ProcessTextUseCase
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import java.time.Instant
import java.time.format.DateTimeParseException

class PrescriptionController(
    private val processImageUseCase: ProcessImageUseCase,
    private val processTextUseCase: ProcessTextUseCase,
    private val getHistoryUseCase: GetLLMExecutionHistoryUseCase,
    private val downloadImageUseCase: DownloadImageUseCase
) {

    suspend fun processImage(call: ApplicationCall) {
        val multipartData = call.extractImageMultipart()
        val imageBytes = multipartData.imageBytes
        val mimeType = multipartData.mimeType

        val resultJsonString = processImageUseCase(imageBytes, mimeType)
        val responseDTO = DefaultJson.decodeFromString<PrescriptionResponseDTO>(resultJsonString)

        call.respond(HttpStatusCode.OK, responseDTO)
    }

    suspend fun processText(call: ApplicationCall) {
        val request = call.receive<TextRequestDTO>()
        val resultJsonString = processTextUseCase(request.text)
        val responseDTO = DefaultJson.decodeFromString<PrescriptionResponseDTO>(resultJsonString)

        call.respond(HttpStatusCode.OK, responseDTO)
    }

    suspend fun getHistory(call: ApplicationCall) {
        val startDateParam = call.request.queryParameters["startDate"]

        val startDate = try {
            startDateParam?.let { Instant.parse(it) } ?: Instant.EPOCH
        } catch (_: DateTimeParseException) {
            throw BusinessException("O formato da data 'startDate' é inválido. Utilize o padrão ISO-8601.")
        }

        val historyList = getHistoryUseCase(startDate).map { it.toDTO() }
        call.respond(HttpStatusCode.OK, historyList)
    }

    suspend fun downloadImage(call: ApplicationCall) {
        val executionId = call.request.queryParameters["executionId"]

        val (imageBytes, format) = downloadImageUseCase(executionId)

        if (imageBytes != null) {
            val contentType = format?.let { ContentType.parse(it.canonicalMimeType) } ?: ContentType.Image.Any
            call.respondBytes(
                bytes = imageBytes,
                contentType = contentType,
                status = HttpStatusCode.OK
            )
        } else {
            throw ResourceNotFoundException("Imagem vinculada a este processamento não foi encontrada no storage.")
        }
    }
}