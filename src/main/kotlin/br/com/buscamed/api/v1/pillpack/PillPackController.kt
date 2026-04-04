package br.com.buscamed.api.v1.pillpack

import br.com.buscamed.api.v1.dto.response.PillPackResponseDTO
import br.com.buscamed.api.v1.extension.extractTextAndImageMultipart
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
import io.ktor.server.response.*
import java.time.Instant
import java.time.format.DateTimeParseException

/**
 * Controlador responsável por orquestrar as requisições relacionadas ao processamento de
 * imagens e textos de cartelas de comprimidos (Pill Pack).
 *
 * @property processImageUseCase Caso de uso para processar imagens diretamente na LLM.
 * @property processTextUseCase Caso de uso para processar textos brutos na LLM.
 * @property getHistoryUseCase Caso de uso para recuperar o histórico de processamentos.
 * @property downloadImageUseCase Caso de uso para realizar o download da imagem original do storage.
 */
class PillPackController(
    private val processImageUseCase: ProcessImageUseCase,
    private val processTextUseCase: ProcessTextUseCase,
    private val getHistoryUseCase: GetLLMExecutionHistoryUseCase,
    private val downloadImageUseCase: DownloadImageUseCase
) {

    /**
     * Processa a requisição multipart enviando a imagem como contexto principal para a LLM.
     *
     * @param call O contexto da requisição Ktor.
     */
    suspend fun processImage(call: ApplicationCall) {
        val multipartData = call.extractTextAndImageMultipart()

        val resultJsonString = processImageUseCase(
            imageBytes = multipartData.imageBytes,
            mimeType = multipartData.mimeType,
            text = multipartData.text,
            clientProcessorVersion = multipartData.pipelineVersion
        )
        val responseDTO = DefaultJson.decodeFromString<PillPackResponseDTO>(resultJsonString)

        call.respond(HttpStatusCode.OK, responseDTO)
    }

    /**
     * Processa a requisição multipart enviando o texto como contexto principal para a LLM.
     *
     * @param call O contexto da requisição Ktor.
     */
    suspend fun processText(call: ApplicationCall) {
        val multipartData = call.extractTextAndImageMultipart()

        val resultJsonString = processTextUseCase(
            text = multipartData.text,
            imageBytes = multipartData.imageBytes,
            mimeType = multipartData.mimeType,
            clientProcessorVersion = multipartData.pipelineVersion
        )
        val responseDTO = DefaultJson.decodeFromString<PillPackResponseDTO>(resultJsonString)

        call.respond(HttpStatusCode.OK, responseDTO)
    }

    /**
     * Recupera o histórico de processamentos realizados, filtrando a partir de uma data inicial.
     *
     * @param call O contexto da requisição Ktor.
     * @throws BusinessException Se a data fornecida for inválida.
     */
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

    /**
     * Realiza o download da imagem original associada a uma execução de processamento.
     *
     * @param call O contexto da requisição Ktor.
     * @throws ResourceNotFoundException Se a imagem não for encontrada no storage.
     */
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
