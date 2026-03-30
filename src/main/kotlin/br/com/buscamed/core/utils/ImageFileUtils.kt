package br.com.buscamed.core.utils

import br.com.buscamed.core.enumeration.SupportedImageFormat

/**
 * Utilitário contendo funções auxiliares para manipulação, validação e
 * resolução de metadados de arquivos de imagem no sistema.
 */
object ImageFileUtils {

    /**
     * Tenta determinar o Mime-Type canônico suportado, usando primeiro
     * o mimetype fornecido e, caso falhe, recorrendo à extensão do arquivo.
     *
     * @param mimeType O Mime-Type original extraído do upload (pode ser impreciso).
     * @param fileName O nome original do arquivo de upload.
     * @return O Mime-Type canônico se o formato for suportado, ou `null` caso contrário.
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
     * Retorna a extensão de arquivo principal associada a um determinado Mime-Type.
     *
     * @param mimeType O Mime-Type a ser consultado.
     * @return A string da extensão (sem o ponto) ou `null` se não for suportado.
     */
    fun getExtensionFromMimeType(mimeType: String): String? {
        return SupportedImageFormat.fromMimeType(mimeType)?.extensions?.firstOrNull()
    }

    /**
     * Valida se um Mime-Type específico corresponde a um dos formatos de
     * imagem configurados como suportados pela aplicação.
     *
     * @param mimeType A string de Mime-Type a ser validada.
     * @return `true` se o Mime-Type for suportado, `false` caso contrário.
     */
    fun isSupported(mimeType: String?): Boolean {
        return SupportedImageFormat.fromMimeType(mimeType) != null
    }
}
