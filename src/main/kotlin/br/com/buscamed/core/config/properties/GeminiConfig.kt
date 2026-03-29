package br.com.buscamed.core.config.properties

/**
 * Configurações para instanciar o cliente do Google Gemini.
 *
 * @property projectId O ID do projeto no Google Cloud.
 * @property location A região onde o modelo será executado.
 */
data class GeminiConfig(
    val projectId: String,
    val location: String
)