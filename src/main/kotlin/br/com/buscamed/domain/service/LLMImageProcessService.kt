package br.com.buscamed.domain.service

import br.com.buscamed.domain.model.LLMProcessResult

/**
 * Serviço responsável por processar o envio de imagens para extração de dados via LLM.
 */
interface LLMImageProcessService {

    /**
     * Processa o conteúdo binário de uma imagem.
     *
     * @param imageBytes Array de bytes da imagem.
     * @param mimeType O tipo MIME correspondente à imagem.
     * @return O resultado do processamento contendo métricas e o texto final.
     */
    suspend fun process(imageBytes: ByteArray, mimeType: String): LLMProcessResult
}