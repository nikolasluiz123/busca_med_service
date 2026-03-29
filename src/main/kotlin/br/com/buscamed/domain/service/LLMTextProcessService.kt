package br.com.buscamed.domain.service

import br.com.buscamed.domain.model.LLMProcessResult

/**
 * Serviço responsável por processar textos para estruturação de dados via LLM.
 */
interface LLMTextProcessService {

    /**
     * Processa a string fornecida e extrai os metadados solicitados.
     *
     * @param text O conteúdo em texto livre.
     * @return O resultado do processamento contendo métricas e o texto final.
     */
    suspend fun process(text: String): LLMProcessResult
}