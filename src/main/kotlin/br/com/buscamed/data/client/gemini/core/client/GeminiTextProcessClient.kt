package br.com.buscamed.data.client.gemini.core.client

import br.com.buscamed.data.client.gemini.core.result.GeminiResult
import io.ktor.server.application.ApplicationEnvironment

abstract class GeminiTextProcessClient(environment: ApplicationEnvironment): GeminiProcessClient(environment) {
    override val modelId: String = "gemini-2.5-flash-lite"
    final override val promptsDirectoryName: String = "gemini/text_process"

    fun process(text: String): GeminiResult {
        val client = getClient()
        val instruction = getSystemInstruction()
        val config = getGenerationConfig()
        val fullPrompt = "$instruction\n\nInput do Usuário:\n\n$text"

        val response = client.models.generateContent(modelId, fullPrompt, config)

        return processResponse(response)
    }
}