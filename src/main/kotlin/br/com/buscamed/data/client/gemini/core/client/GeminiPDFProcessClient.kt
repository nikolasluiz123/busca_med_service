package br.com.buscamed.data.client.gemini.core.client

import br.com.buscamed.core.config.properties.GeminiConfig
import br.com.buscamed.domain.model.LLMProcessResult
import br.com.buscamed.domain.service.LLMImageProcessService
import br.com.buscamed.domain.service.LLMPDFProcessService
import com.google.genai.types.Blob
import com.google.genai.types.Content
import com.google.genai.types.Part

/**
 * Cliente base para o processamento de arquivos PDF utilizando os modelos do Google Gemini.
 *
 * Implementa [LLMImageProcessService] com regras específicas para conversão dos bytes e
 * envio do conteúdo ao modelo gerativo.
 *
 * @param config As propriedades de configuração para conexão com a API do Gemini.
 */
abstract class GeminiPDFProcessClient(config: GeminiConfig): GeminiProcessClient(config), LLMPDFProcessService {
    override val modelId: String = "gemini-2.5-flash-lite"
    final override val promptsDirectoryName: String = "gemini/pdf_process"

    override suspend fun process(pdfBytes: ByteArray): LLMProcessResult {
        val client = getClient()
        val instruction = getSystemInstruction()
        val config = getGenerationConfig()

        val textPart = Part.builder().text(instruction).build()
        val inlineData = Blob.builder().data(pdfBytes).mimeType("application/pdf").build()
        val pdfPart = Part.builder().inlineData(inlineData).build()

        val content = Content.builder()
            .parts(listOf(textPart, pdfPart))
            .role("user")
            .build()

        val response = client.models.generateContent(modelId, content, config)

        return processResponse(response)
    }
}
