package br.com.buscamed.core.utils

import br.com.buscamed.core.enumeration.SupportedImageFormat

object ImageFileUtils {

    /**
     * Resolve o MimeType baseado na string fornecida ou na extensão do arquivo.
     * Retorna null se não for possível determinar um formato suportado.
     */
    fun resolveMimeType(mimeType: String?, fileName: String?): String? {
        val formatFromMime = SupportedImageFormat.fromMimeType(mimeType)

        if (formatFromMime != null) {
            return formatFromMime.canonicalMimeType
        }

        if (!fileName.isNullOrBlank()) {
            val extension = fileName.substringAfterLast('.', "").lowercase()
            val formatFromExt = SupportedImageFormat.entries.find { it.extensions.contains(extension) }

            if (formatFromExt != null) {
                return formatFromExt.canonicalMimeType
            }
        }

        return null
    }

    /**
     * Retorna a extensão padrão para um MimeType.
     * Retorna null se o MimeType não for suportado.
     */
    fun getExtensionFromMimeType(mimeType: String): String? {
        return SupportedImageFormat.fromMimeType(mimeType)?.extensions?.firstOrNull()
    }

    /**
     * Verifica se o MimeType é suportado pelo sistema.
     */
    fun isSupported(mimeType: String?): Boolean {
        return SupportedImageFormat.fromMimeType(mimeType) != null
    }
}