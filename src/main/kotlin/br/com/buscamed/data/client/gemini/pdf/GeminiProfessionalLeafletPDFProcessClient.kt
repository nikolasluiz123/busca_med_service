package br.com.buscamed.data.client.gemini.pdf

import br.com.buscamed.core.config.properties.GeminiConfig
import br.com.buscamed.data.client.gemini.core.client.GeminiPDFProcessClient
import br.com.buscamed.data.client.gemini.core.schema.ProfessionalLeafletSchemaFactory
import com.google.genai.types.Schema

class GeminiProfessionalLeafletPDFProcessClient(config: GeminiConfig): GeminiPDFProcessClient(config) {
    override val promptVersion: String = "v1"
    override val promptFileName: String = "professional_leaflet"
    override val modelId: String = "gemini-flash-latest"

    override fun getUserFailureGenericMessage(): String {
        return "Não foi possível extrair as informações da bula para profissionais. Se o erro persistir, contate o suporte"
    }

    override fun getResponseSchema(): Schema {
        return ProfessionalLeafletSchemaFactory.createSchema()
    }
}