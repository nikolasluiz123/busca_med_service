package br.com.buscamed.domain.repository

import br.com.buscamed.domain.model.LLMExecutionHistory
import java.time.Instant

/**
 * Contrato de repositório para o gerenciamento do histórico de execuções de Large Language Models (LLMs).
 */
interface LLMExecutionHistoryRepository {

    /**
     * Salva um novo registro de histórico de execução de LLM.
     *
     * @param history O objeto [LLMExecutionHistory] a ser persistido.
     * @return O identificador único gerado para o histórico salvo.
     */
    suspend fun save(history: LLMExecutionHistory): String

    /**
     * Atualiza o caminho de armazenamento da imagem processada para um registro de histórico existente.
     *
     * @param historyId O identificador único do registro histórico.
     * @param path O caminho no Storage onde a imagem foi salva.
     */
    suspend fun updateImageStoragePath(historyId: String, path: String)

    /**
     * Recupera a lista de históricos de execução de LLM a partir de uma data específica.
     *
     * @param startDate A data inicial para a busca dos registros.
     * @return Uma lista de [LLMExecutionHistory] encontrados após a data informada.
     */
    suspend fun findHistorySince(startDate: Instant): List<LLMExecutionHistory>

    /**
     * Busca um registro específico de histórico pelo seu identificador único.
     *
     * @param historyId O identificador do histórico desejado.
     * @return O objeto [LLMExecutionHistory] correspondente ou nulo se não for encontrado.
     */
    suspend fun findHistoryById(historyId: String): LLMExecutionHistory?
}
