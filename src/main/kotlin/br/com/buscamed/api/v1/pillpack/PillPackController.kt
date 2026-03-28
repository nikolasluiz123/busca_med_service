package br.com.buscamed.api.v1.pillpack

import br.com.buscamed.api.v1.dto.request.TextRequestDTO
import br.com.buscamed.api.v1.extensions.extractImageMultipart
import br.com.buscamed.data.mapper.toDTO
import br.com.buscamed.domain.usecase.GetPillPackHistoryUseCase
import br.com.buscamed.domain.usecase.ProcessPillPackImageUseCase
import br.com.buscamed.domain.usecase.ProcessPillPackTextUseCase
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import java.time.Instant
import java.time.format.DateTimeParseException

/**
 * Controlador responsável por orquestrar as requisições relacionadas a cartelas de comprimidos.
 *
 * @property processImageUseCase Caso de uso para processamento de imagens de cartelas.
 * @property processTextUseCase Caso de uso para processamento de textos de cartelas.
 * @property getHistoryUseCase Caso de uso para resgate de histórico.
 */
class PillPackController(
    private val processImageUseCase: ProcessPillPackImageUseCase,
    private val processTextUseCase: ProcessPillPackTextUseCase,
    private val getHistoryUseCase: GetPillPackHistoryUseCase
) {

    /**
     * Processa a requisição de extração de dados a partir de uma imagem de cartela de comprimidos.
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
     * Processa a requisição de extração de dados a partir de um texto de cartela de comprimidos.
     *
     * @param call O contexto da requisição Ktor.
     */
    suspend fun processText(call: ApplicationCall) {
        val request = call.receive<TextRequestDTO>()
        val result = processTextUseCase(request.text)

        call.respond(HttpStatusCode.OK, result)
    }

    /**
     * Processa a requisição para obter o histórico de processamentos de cartelas de comprimidos.
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
}