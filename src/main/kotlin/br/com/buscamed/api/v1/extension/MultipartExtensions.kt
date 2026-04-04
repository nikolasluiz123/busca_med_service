package br.com.buscamed.api.v1.extension

import br.com.buscamed.api.v1.dto.request.TextAndImageMultipartDTO
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receiveMultipart
import io.ktor.utils.io.readRemaining
import kotlinx.io.readByteArray

/**
 * Extrai os dados de imagem, tipo MIME e campo de texto de uma requisição Multipart.
 *
 * @return Um objeto [TextAndImageMultipartDTO] contendo as informações extraídas.
 */
suspend fun ApplicationCall.extractTextAndImageMultipart(): TextAndImageMultipartDTO {
    val multipart = receiveMultipart()
    var text: String? = null
    var imageBytes: ByteArray? = null
    var mimeType: String? = null
    var pipelineVersion: String? = null

    multipart.forEachPart { part ->
        when (part) {
            is PartData.FormItem -> {
                if (part.name == "text") {
                    text = part.value
                }

                if (part.name == "pipelineVersion") {
                    pipelineVersion = part.value
                }
            }
            is PartData.FileItem -> {
                if (part.name == "image") {
                    imageBytes = part.provider().readRemaining().readByteArray()
                    mimeType = part.contentType?.toString()
                }
            }
            else -> {}
        }
        part.dispose()
    }

    return TextAndImageMultipartDTO(text, imageBytes, mimeType, pipelineVersion)
}