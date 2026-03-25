package br.com.buscamed.api.v1.dto.request

/**
 * DTO interno para transporte de dados extraídos de requisições Multipart.
 *
 * @property imageBytes Array de bytes contendo os dados da imagem.
 * @property mimeType Tipo MIME da imagem (ex: "image/jpeg").
 */
class ImageMultipartDTO(
    val imageBytes: ByteArray?,
    val mimeType: String?
)