package br.com.buscamed.domain.model

/**
 * Entidade que encapsula o resultado do processamento de uma LLM.
 *
 * @property resultText O texto bruto retornado pelo modelo (formato JSON em string).
 * @property inputTokens Quantidade de tokens enviados no prompt.
 * @property outputTokens Quantidade de tokens gerados pelo modelo.
 * @property promptName O nome do arquivo de prompt utilizado.
 * @property llmModel O modelo de LLM que foi utilizado (ex: gemini-2.5-flash-lite).
 */
data class LLMProcessResult(
    val resultText: String,
    val inputTokens: Int,
    val outputTokens: Int,
    val promptName: String,
    val llmModel: String
)