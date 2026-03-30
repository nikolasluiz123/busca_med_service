package br.com.buscamed.domain.model

import java.time.Instant

/**
 * Entidade que armazena os registros e métricas de uma execução envolvendo chamadas a LLMs.
 *
 * @property inputTokens Número de tokens enviados na requisição (prompt e conteúdo).
 * @property outputTokens Número de tokens gerados na resposta da LLM.
 * @property result O conteúdo do resultado bruto retornado ou nulo em caso de erro sem retorno.
 * @property success Indica se o processamento completo ocorreu sem erros de integração.
 * @property startDate Momento em que a requisição de processamento iniciou.
 * @property endDate Momento em que o processamento finalizou e o resultado foi retornado.
 * @property id Identificador único do registro (gerado pelo repositório).
 * @property storageImagePath Opcional. Caminho do arquivo original, como imagem no Storage.
 * @property prompt O identificador ou versão do prompt que foi utilizado.
 */
data class LLMExecutionHistory(
    val inputTokens: Int,
    val outputTokens: Int,
    val result: String?,
    val success: Boolean,
    val startDate: Instant,
    val endDate: Instant,
    val id: String? = null,
    val storageImagePath: String? = null,
    val prompt: String = ""
)
