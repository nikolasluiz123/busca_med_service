package br.com.buscamed.api.v1.dto.request

/**
 * DTO interno para transporte de dados extraídos de requisições Multipart que contêm texto e imagem.
 *
 * @property text O texto extraído previamente pelo cliente (ex: MLKit).
 * @property imageBytes Array de bytes contendo os dados da imagem original.
 * @property mimeType Tipo MIME da imagem (ex: "image/jpeg").
 * @property pipelineVersion Versão do pipeline de execução do client
 */
class TextAndImageMultipartDTO(
    val text: String?,
    val imageBytes: ByteArray?,
    val mimeType: String?,
    val pipelineVersion: String?
)