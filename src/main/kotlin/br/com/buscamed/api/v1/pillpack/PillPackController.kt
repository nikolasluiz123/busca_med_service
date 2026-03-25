package br.com.buscamed.api.v1.pillpack

import br.com.buscamed.api.v1.dto.request.TextRequestDTO
import br.com.buscamed.api.v1.extensions.extractImageMultipart
import br.com.buscamed.domain.usecase.ProcessPillPackImageUseCase
import br.com.buscamed.domain.usecase.ProcessPillPackTextUseCase
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.response.respond

/**
 * Controlador responsável por orquestrar as requisições relacionadas a cartelas de comprimidos.
 *
 * @property processImageUseCase Caso de uso para processamento de imagens de cartelas.
 * @property processTextUseCase Caso de uso para processamento de textos de cartelas.
 */
class PillPackController(
    private val processImageUseCase: ProcessPillPackImageUseCase,
    private val processTextUseCase: ProcessPillPackTextUseCase
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
}