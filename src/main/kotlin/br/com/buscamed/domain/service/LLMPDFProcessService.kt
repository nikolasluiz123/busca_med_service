package br.com.buscamed.domain.service

import br.com.buscamed.domain.model.LLMProcessResult

/**
 * Serviço responsável por processar o envio de PDFs para extração de dados via LLM.
 */
interface LLMPDFProcessService {

    /**
     * Processa o conteúdo binário do PDF.
     *
     * @param pdfBytes Array de bytes da imagem.
     * @return O resultado do processamento contendo métricas e o texto final.
     */
    suspend fun process(pdfBytes: ByteArray): LLMProcessResult
}