package br.com.buscamed.api.v1.prescription

import br.com.buscamed.api.v1.dto.request.TextRequestDTO
import br.com.buscamed.api.v1.extensions.extractImageMultipart
import br.com.buscamed.domain.usecase.ProcessMedicalPrescriptionImageUseCase
import br.com.buscamed.domain.usecase.ProcessMedicalPrescriptionTextUseCase
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.response.respond

/**
 * Controlador responsável por orquestrar as requisições relacionadas a prescrições médicas.
 *
 * @property processImageUseCase Caso de uso para processamento de imagens de prescrições.
 * @property processTextUseCase Caso de uso para processamento de textos de prescrições.
 */
class PrescriptionController(
    private val processImageUseCase: ProcessMedicalPrescriptionImageUseCase,
    private val processTextUseCase: ProcessMedicalPrescriptionTextUseCase
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
}