package br.com.buscamed.data.client.gemini.core.client

import br.com.buscamed.core.config.properties.GeminiConfig
import br.com.buscamed.data.client.core.llm.LLMProcessClient
import br.com.buscamed.data.client.gemini.core.exception.GeminiErrorCodes
import br.com.buscamed.data.client.gemini.core.exception.GeminiIntegrationException
import br.com.buscamed.domain.model.LLMProcessResult
import com.google.genai.Client
import com.google.genai.types.GenerateContentConfig
import com.google.genai.types.GenerateContentResponse
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

abstract class GeminiProcessClient(
    private val config: GeminiConfig
): LLMProcessClient() {

    protected abstract fun getUserFailureGenericMessage(): String

    protected fun getClient(): Client {
        return Client.builder()
            .project(config.projectId)
            .location(config.location)
            .vertexAI(true)
            .build()
    }

    protected fun getGenerationConfig(): GenerateContentConfig {
        return GenerateContentConfig.builder()
            .responseMimeType("application/json")
            .temperature(0.0f)
            .build()
    }

    protected fun processResponse(response: GenerateContentResponse): LLMProcessResult {
        val usage = response.usageMetadata()?.get()
        val inputTokens = usage?.promptTokenCount()?.get() ?: 0
        val outputTokens = usage?.candidatesTokenCount()?.get() ?: 0

        val outputText = response.candidates()
            .map { it.firstOrNull()?.content()?.get()?.parts()?.get()?.firstOrNull()?.text()?.get() }
            .orElse("{}") ?: "{}"

        val jsonElement = Json.parseToJsonElement(outputText)

        return if (jsonElement is JsonObject) {
            LLMProcessResult(
                resultText = jsonElement.toString(),
                inputTokens = inputTokens,
                outputTokens = outputTokens,
                promptName = getFullPromptFileName()
            )
        } else {
            throw GeminiIntegrationException(
                userMessage = getUserFailureGenericMessage(),
                technicalMessage = """"
                Erro ao converter o retorno da LLM para um objeto JSON.
                Retorno da LLM: 
                $outputText
                """".trimMargin(),
                statusCode = 502,
                errorCode = GeminiErrorCodes.GOOGLE_GEMINI_MALFORMED_RESULT,
                serviceName = this.javaClass.simpleName
            )
        }
    }
}