package br.com.buscamed.data.client.gemini.core.client

import br.com.buscamed.core.config.properties.GeminiConfig
import br.com.buscamed.domain.model.LLMProcessResult
import br.com.buscamed.domain.service.LLMTextProcessService

/**
 * Cliente base para o processamento de texto utilizando os modelos do Google Gemini.
 *
 * Implementa [LLMTextProcessService] e concatena a instrução de sistema e a
 * entrada do usuário em um prompt único submetido ao modelo.
 *
 * @param config As propriedades de configuração para conexão com a API do Gemini.
 */
abstract class GeminiTextProcessClient(config: GeminiConfig): GeminiProcessClient(config), LLMTextProcessService {
    override val modelId: String = "gemini-2.5-flash-lite"
    final override val promptsDirectoryName: String = "gemini/text_process"

    override suspend fun process(text: String): LLMProcessResult {
        val client = getClient()
        val instruction = getSystemInstruction()
        val config = getGenerationConfig()
        val fullPrompt = "$instruction\n\nInput do Usuário:\n\n$text"

        val response = client.models.generateContent(modelId, fullPrompt, config)

        return processResponse(response)
    }
}
