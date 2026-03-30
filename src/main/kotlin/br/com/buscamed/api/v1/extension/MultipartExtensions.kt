package br.com.buscamed.api.v1.extension

import br.com.buscamed.api.v1.dto.request.ImageMultipartDTO
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receiveMultipart
import io.ktor.utils.io.readRemaining
import kotlinx.io.readByteArray

/**
 * Extrai os dados de imagem e o tipo MIME de uma requisição Multipart.
 *
 * @return Um objeto [ImageMultipartDTO] contendo os bytes da imagem e o MIME type extraídos.
 */
suspend fun ApplicationCall.extractImageMultipart(): ImageMultipartDTO {
    val multipart = receiveMultipart()
    var imageBytes: ByteArray? = null
    var mimeType: String? = null

    multipart.forEachPart { part ->
        when (part) {
            is PartData.FileItem -> {
                if (part.name == "image") {
                    imageBytes = part.provider().readRemaining().readByteArray()
                }
            }
            is PartData.FormItem -> {
                if (part.name == "mimeType") {
                    val value = part.value.trim()
                    if (value.isNotBlank()) {
                        mimeType = value
                    }
                }
            }
            else -> {}
        }
        part.dispose()
    }

    return ImageMultipartDTO(imageBytes, mimeType)
}