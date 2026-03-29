package br.com.buscamed.api.v1.prescription

import br.com.buscamed.api.v1.dto.request.TextRequestDTO
import br.com.buscamed.api.v1.extensions.extractImageMultipart
import br.com.buscamed.data.mapper.toDTO
import br.com.buscamed.domain.usecase.DownloadImageUseCase
import br.com.buscamed.domain.usecase.GetMedicalPrescriptionHistoryUseCase
import br.com.buscamed.domain.usecase.ProcessMedicalPrescriptionImageUseCase
import br.com.buscamed.domain.usecase.ProcessMedicalPrescriptionTextUseCase
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondBytes
import java.time.Instant
import java.time.format.DateTimeParseException

/**
 * Controlador responsável por orquestrar as requisições relacionadas a prescrições médicas.
 *
 * @property processImageUseCase Caso de uso para processamento de imagens de prescrições.
 * @property processTextUseCase Caso de uso para processamento de textos de prescrições.
 * @property getHistoryUseCase Caso de uso para resgate de histórico.
 * @property downloadImageUseCase Caso de uso para download da imagem.
 */
class PrescriptionController(
    private val processImageUseCase: ProcessMedicalPrescriptionImageUseCase,
    private val processTextUseCase: ProcessMedicalPrescriptionTextUseCase,
    private val getHistoryUseCase: GetMedicalPrescriptionHistoryUseCase,
    private val downloadImageUseCase: DownloadImageUseCase
) {

    /**
     * Processa a requisição de extração de dados a partir de uma imagem de prescrição.
     *
     * @param call O contexto da requisição Ktor.
     */
    suspend fun processImage(call: ApplicationCall) {
        val multipartData = call.extractImageMultipart()
        val imageBytes = multipartData.imageBytes
        val mimeType = multipartData.mimeType

        val result = processImageUseCase(imageBytes, mimeType)
        call.respond(HttpStatusCode.OK, result)
    }

    /**
     * Processa a requisição de extração de dados a partir de um texto de prescrição.
     *
     * @param call O contexto da requisição Ktor.
     */
    suspend fun processText(call: ApplicationCall) {
        val request = call.receive<TextRequestDTO>()
        val result = processTextUseCase(request.text)
        
        call.respond(HttpStatusCode.OK, result)
    }

    /**
     * Processa a requisição para obter o histórico de processamentos de prescrição médica.
     * Recupera o parâmetro 'startDate' da query string e realiza a busca filtrada.
     *
     * @param call O contexto da requisição Ktor.
     */
    suspend fun getHistory(call: ApplicationCall) {
        val startDateParam = call.request.queryParameters["startDate"]

        val startDate = try {
            startDateParam?.let { Instant.parse(it) } ?: Instant.EPOCH
        } catch (e: DateTimeParseException) {
            call.respond(HttpStatusCode.BadRequest, "O formato da data 'startDate' é inválido. Utilize o padrão ISO-8601.")
            return
        }

        val historyList = getHistoryUseCase(startDate).map { it.toDTO() }
        call.respond(HttpStatusCode.OK, historyList)
    }

    /**
     * Processa a requisição para realizar o download da imagem de prescrição médica.
     *
     * @param call O contexto da requisição Ktor.
     */
    suspend fun downloadImage(call: ApplicationCall) {
        val executionId = call.request.queryParameters["executionId"]
        
        val (imageBytes, contentType) = downloadImageUseCase(executionId)

        if (imageBytes != null) {
            call.respondBytes(
                bytes = imageBytes, 
                contentType = contentType,
                status = HttpStatusCode.OK
            )
        } else {
            call.respond(HttpStatusCode.NoContent)
        }
    }
}