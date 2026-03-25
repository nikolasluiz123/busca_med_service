package br.com.buscamed.core.enumeration

import io.ktor.http.ContentType

enum class SupportedImageFormat(val mimeTypes: List<String>, val extensions: List<String>) {
    JPEG(listOf("image/jpeg", "image/jpg"), listOf("jpg", "jpeg")),
    PNG(listOf("image/png"), listOf("png")),
    WEBP(listOf("image/webp"), listOf("webp"));

    val canonicalMimeType: String get() = mimeTypes.first()

    companion object {
        fun fromMimeType(mimeType: String?): SupportedImageFormat? {
            if (mimeType.isNullOrBlank()) return null

            val cleanMime = try {
                ContentType.parse(mimeType).run { "$contentType/$contentSubtype" }
            } catch (e: Exception) {
                mimeType.trim()
            }

            return SupportedImageFormat.entries.find { format ->
                format.mimeTypes.any { it.equals(cleanMime, ignoreCase = true) }
            }
        }
    }
}

