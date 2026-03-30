package br.com.buscamed.domain.usecase

import br.com.buscamed.domain.model.LLMExecutionHistory
import br.com.buscamed.domain.repository.LLMExecutionHistoryRepository
import br.com.buscamed.domain.service.LLMTextProcessService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant

/**
 * Caso de uso universal para extração de dados estruturados a partir de texto base utilizando LLMs.
 *
 * Este caso de uso invoca o serviço de processamento de texto e registra o histórico
 * da execução no banco de dados, incluindo métricas de tokens e o status de sucesso.
 *
 * @property executionHistoryRepository O repositório para salvar o histórico da execução.
 * @property llmProcessService O serviço responsável pela comunicação com o modelo de linguagem.
 */
class ProcessTextUseCase(
    private val executionHistoryRepository: LLMExecutionHistoryRepository,
    private val llmProcessService: LLMTextProcessService
) {

    /**
     * Executa o processo de extração de dados do texto.
     *
     * @param text O texto bruto a ser processado pela LLM.
     * @return O resultado processado pela LLM em formato de string JSON.
     * @throws Exception Se ocorrer algum erro durante a comunicação com o serviço da LLM.
     */
    suspend operator fun invoke(text: String): String = withContext(Dispatchers.IO) {
        var executionSuccess = true
        val executionStart = Instant.now()
        var inputTokens = 0
        var outputTokens = 0
        var resultText = "{}"
        var prompt = ""

        try {
            val llmResult = llmProcessService.process(text)
            inputTokens = llmResult.inputTokens
            outputTokens = llmResult.outputTokens
            resultText = llmResult.resultText
            prompt = llmResult.promptName

            resultText
        } catch (e: Exception) {
            executionSuccess = false
            throw e
        } finally {
            val history = LLMExecutionHistory(
                inputTokens = inputTokens,
                outputTokens = outputTokens,
                result = resultText,
                success = executionSuccess,
                startDate = executionStart,
                endDate = Instant.now(),
                prompt = prompt
            )

            executionHistoryRepository.save(history)
        }
    }
}
