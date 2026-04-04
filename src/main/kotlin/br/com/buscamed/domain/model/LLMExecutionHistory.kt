package br.com.buscamed.domain.model

import br.com.buscamed.domain.model.enumeration.ExecutionType
import java.time.Instant

/**
 * Entidade que armazena os registros e métricas de uma execução envolvendo chamadas a LLMs.
 *
 * @property type O tipo de processamento realizado (imagem ou texto).
 * @property inputText O texto fornecido como entrada para a execução (ex: texto extraído via OCR).
 * @property inputTokens Número de tokens enviados na requisição (prompt e conteúdo).
 * @property outputTokens Número de tokens gerados na resposta da LLM.
 * @property result O conteúdo do resultado bruto retornado ou nulo em caso de erro sem retorno.
 * @property success Indica se o processamento completo ocorreu sem erros de integração.
 * @property startDate Momento em que a requisição de processamento iniciou.
 * @property endDate Momento em que o processamento finalizou e o resultado foi retornado.
 * @property id Identificador único do registro (gerado pelo repositório).
 * @property storageImagePath Opcional. Caminho do arquivo original, como imagem no Storage.
 * @property prompt O identificador ou versão do prompt que foi utilizado.
 * @property clientProcessorVersion Versão do pipeline de processamento do client
 * @property llmModel O modelo de LLM que foi utilizado (ex: gemini-2.5-flash-lite).
 */
data class LLMExecutionHistory(
    val type: ExecutionType,
    val inputText: String,
    val inputTokens: Int,
    val outputTokens: Int,
    val result: String?,
    val success: Boolean,
    val startDate: Instant,
    val endDate: Instant,
    val id: String? = null,
    val storageImagePath: String? = null,
    val prompt: String = "",
    val clientProcessorVersion: String = "",
    val llmModel: String
)
