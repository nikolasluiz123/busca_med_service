package br.com.buscamed.data.client.gemini.text

import br.com.buscamed.core.config.properties.GeminiConfig
import br.com.buscamed.data.client.gemini.core.client.GeminiTextProcessClient
import br.com.buscamed.data.client.gemini.core.schema.PillPackSchemaFactory
import com.google.genai.types.Schema

class GeminiPillPackTextProcessClient(config: GeminiConfig): GeminiTextProcessClient(config) {
    override val promptVersion: String = "v2"
    override val promptFileName: String = "pill_pack"

    override fun getUserFailureGenericMessage(): String {
        return "Não foi possível extrair as informações da imagem da sua cartela de comprimidos. Se o erro persistir, contate o suporte"
    }

    override fun getResponseSchema(): Schema {
        return PillPackSchemaFactory.createSchema()
    }
}