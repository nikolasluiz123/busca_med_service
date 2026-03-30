package br.com.buscamed.domain.usecase

import br.com.buscamed.core.enumeration.SupportedImageFormat

/**
 * Mapeia uma extensão de arquivo para o tipo de imagem suportada pelo domínio.
 */
class GetSupportedImageFormatUseCase {

    /**
     * Resolve uma extensão de arquivo e retorna o respectivo [SupportedImageFormat].
     *
     * @param extension A extensão do arquivo de imagem (ex: "png", "jpg").
     * @return O formato suportado correspondente, ou null se não for suportado.
     */
    operator fun invoke(extension: String): SupportedImageFormat? {
        return SupportedImageFormat.entries.find { it.extensions.contains(extension.lowercase()) }
    }
}
