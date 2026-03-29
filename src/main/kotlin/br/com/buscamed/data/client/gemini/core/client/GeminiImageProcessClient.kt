package br.com.buscamed.data.client.gemini.core.client

import br.com.buscamed.core.config.properties.GeminiConfig
import br.com.buscamed.domain.model.LLMProcessResult
import br.com.buscamed.domain.service.LLMImageProcessService
import com.google.genai.types.Blob
import com.google.genai.types.Content
import com.google.genai.types.Part

abstract class GeminiImageProcessClient(config: GeminiConfig): GeminiProcessClient(config), LLMImageProcessService {
    override val modelId: String = "gemini-2.5-flash-lite"
    final override val promptsDirectoryName: String = "gemini/image_process"

    override suspend fun process(imageBytes: ByteArray, mimeType: String): LLMProcessResult {
        val client = getClient()
        val instruction = getSystemInstruction()
        val config = getGenerationConfig()

        val textPart = Part.builder().text(instruction).build()
        val inlineData = Blob.builder().data(imageBytes).mimeType(mimeType).build()
        val imagePart = Part.builder().inlineData(inlineData).build()

        val content = Content.builder()
            .parts(listOf(textPart, imagePart))
            .role("user")
            .build()

        val response = client.models.generateContent(modelId, content, config)

        return processResponse(response)
    }
}