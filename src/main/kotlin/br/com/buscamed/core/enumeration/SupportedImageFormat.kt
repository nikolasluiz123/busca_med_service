package br.com.buscamed.core.enumeration

/**
 * Enumeração dos formatos de imagem suportados pelo sistema e, consequentemente,
 * pelos modelos de processamento visual utilizados.
 *
 * @property mimeTypes Lista de Mime-Types que correspondem a este formato (ex: image/jpeg).
 * @property extensions Lista de extensões de arquivo que representam este formato (ex: jpg, jpeg).
 */
enum class SupportedImageFormat(val mimeTypes: List<String>, val extensions: List<String>) {
    JPEG(listOf("image/jpeg", "image/jpg"), listOf("jpg", "jpeg")),
    PNG(listOf("image/png"), listOf("png")),
    WEBP(listOf("image/webp"), listOf("webp"));

    /** O Mime-Type principal oficial considerado como padrão para o formato. */
    val canonicalMimeType: String get() = mimeTypes.first()

    companion object {
        /**
         * Resolve o formato de imagem suportado baseado no Mime-Type fornecido.
         *
         * Limpa informações extras do Mime-Type (como charset) antes da comparação.
         *
         * @param mimeType O Mime-Type a ser resolvido (ex: "image/jpeg; charset=UTF-8").
         * @return O respectivo [SupportedImageFormat] se existir correspondência, ou `null` caso contrário.
         */
        fun fromMimeType(mimeType: String?): SupportedImageFormat? {
            if (mimeType.isNullOrBlank()) return null

            val cleanMime = mimeType.trim().substringBefore(";")

            return entries.find { format ->
                format.mimeTypes.any { it.equals(cleanMime, ignoreCase = true) }
            }
        }
    }
}
