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

/**
 * Cliente abstrato que encapsula a lógica comum para interagir com a API do Google Gemini via Vertex AI.
 *
 * Esta classe gerencia a configuração do cliente, a configuração da geração de conteúdo e o
 * processamento da resposta, garantindo que a saída seja um JSON válido.
 *
 * @param config As propriedades de configuração para a API do Gemini.
 */
abstract class GeminiProcessClient(
    private val config: GeminiConfig
): LLMProcessClient() {

    /**
     * Retorna a mensagem de falha genérica que será exibida ao usuário final
     * caso o processamento da LLM falhe de uma maneira inesperada.
     */
    protected abstract fun getUserFailureGenericMessage(): String

    /**
     * Constrói e retorna uma instância do cliente Gemini configurado para usar Vertex AI.
     *
     * @return Uma instância de [Client] pronta para uso.
     */
    protected fun getClient(): Client {
        return Client.builder()
            .project(config.projectId)
            .location(config.location)
            .vertexAI(true)
            .build()
    }

    /**
     * Cria a configuração para a geração de conteúdo do modelo Gemini.
     *
     * Força a resposta para o tipo "application/json" e define a temperatura como 0.0f
     * para obter resultados mais determinísticos e consistentes.
     *
     * @return Um objeto [GenerateContentConfig] configurado.
     */
    protected fun getGenerationConfig(): GenerateContentConfig {
        return GenerateContentConfig.builder()
            .responseMimeType("application/json")
            .temperature(0.0f)
            .build()
    }

    /**
     * Processa a resposta da API Gemini, extrai o conteúdo e os metadados de uso.
     *
     * @param response O objeto de resposta recebido da API Gemini.
     * @return Um [LLMProcessResult] contendo o texto do resultado, contagem de tokens e nome do prompt.
     * @throws GeminiIntegrationException Se o texto retornado pela LLM não for um objeto JSON válido.
     */
    protected fun processResponse(response: GenerateContentResponse): LLMProcessResult {
        val usage = response.usageMetadata()?.get()
        val inputTokens = usage?.promptTokenCount()?.get() ?: 0
        val outputTokens = usage?.candidatesTokenCount()?.get() ?: 0

        val outputText = response.candidates()
            .map { it.firstOrNull()?.content()?.get()?.parts()?.get()?.firstOrNull()?.text()?.get() }
            .orElse("{}") ?: "{}"

        val jsonElement = try {
            Json.parseToJsonElement(outputText)
        } catch (e: Exception) {
            logger.error("Erro ao converter o retorno da LLM para um objeto JSON", e)
            null
        }

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
