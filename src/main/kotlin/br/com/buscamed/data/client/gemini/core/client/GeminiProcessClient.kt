package br.com.buscamed.data.client.gemini.core.client

import com.google.genai.Client
import com.google.genai.types.GenerateContentConfig
import com.google.genai.types.GenerateContentResponse
import br.com.buscamed.data.client.core.llm.LLMProcessClient
import br.com.buscamed.data.client.gemini.core.exception.GeminiErrorCodes
import br.com.buscamed.data.client.gemini.core.exception.GeminiIntegrationException
import br.com.buscamed.data.client.gemini.core.result.GeminiResult
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationEnvironment
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

abstract class GeminiProcessClient(environment: ApplicationEnvironment): LLMProcessClient() {
    protected val projectId = environment.config.property("buscamed.gcp.project_id").getString()
    protected val location = environment.config.property("buscamed.gcp.region").getString()

    protected abstract fun getUserFailureGenericMessage(): String

    protected fun getClient(): Client {
        return Client.builder()
            .project(projectId)
            .location(location)
            .vertexAI(true)
            .build()
    }

    protected fun getGenerationConfig(): GenerateContentConfig {
        return GenerateContentConfig.builder()
            .responseMimeType("application/json")
            .temperature(0.0f)
            .build()
    }

    protected fun processResponse(response: GenerateContentResponse): GeminiResult {
        val usage = response.usageMetadata()?.get()
        val inputTokens = usage?.promptTokenCount()?.get() ?: 0
        val outputTokens = usage?.candidatesTokenCount()?.get() ?: 0

        val outputText = response.candidates()
            .map { it.firstOrNull()?.content()?.get()?.parts()?.get()?.firstOrNull()?.text()?.get() }
            .orElse("{}") ?: "{}"

        val jsonElement = Json.parseToJsonElement(outputText)

        return if (jsonElement is JsonObject) {
            GeminiResult(
                json = jsonElement,
                inputTokens = inputTokens,
                outputTokens = outputTokens,
                promptFileName = getFullPromptFileName()
            )
        } else {
            throw GeminiIntegrationException(
                userMessage = getUserFailureGenericMessage(),
                technicalMessage = """"
                Erro ao converter o retorno da LLM para um objeto JSON.
                Retorno da LLM: 
                $outputText
                """".trimMargin(),
                statusCode = HttpStatusCode.BadGateway,
                errorCode = GeminiErrorCodes.GOOGLE_GEMINI_MALFORMED_RESULT,
                serviceName = this.javaClass.simpleName
            )
        }
    }
}