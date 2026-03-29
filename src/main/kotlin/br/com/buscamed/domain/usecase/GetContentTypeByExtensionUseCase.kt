package br.com.buscamed.domain.usecase

import io.ktor.http.ContentType

class GetContentTypeByExtensionUseCase {
    operator fun invoke(extension: String): ContentType {
        return when (extension.lowercase()) {
            "jpg", "jpeg" -> ContentType.Image.JPEG
            "png" -> ContentType.Image.PNG
            "gif" -> ContentType.Image.GIF
            "webp" -> ContentType.parse("image/webp")
            else -> ContentType.Image.Any
        }
    }
}