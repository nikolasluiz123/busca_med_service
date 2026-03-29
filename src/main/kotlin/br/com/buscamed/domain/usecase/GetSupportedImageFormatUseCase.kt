package br.com.buscamed.domain.usecase

import br.com.buscamed.core.enumeration.SupportedImageFormat

/**
 * Mapeia uma extensão de arquivo para o tipo de imagem suportada pelo domínio.
 */
class GetSupportedImageFormatUseCase {
    operator fun invoke(extension: String): SupportedImageFormat? {
        return SupportedImageFormat.entries.find { it.extensions.contains(extension.lowercase()) }
    }
}