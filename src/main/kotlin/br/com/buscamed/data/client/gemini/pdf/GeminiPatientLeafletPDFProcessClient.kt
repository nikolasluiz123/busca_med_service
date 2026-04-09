package br.com.buscamed.data.client.gemini.pdf

import br.com.buscamed.core.config.properties.GeminiConfig
import br.com.buscamed.data.client.gemini.core.client.GeminiPDFProcessClient
import br.com.buscamed.data.client.gemini.core.schema.PatientLeafletSchemaFactory
import com.google.genai.types.Schema

class GeminiPatientLeafletPDFProcessClient(config: GeminiConfig): GeminiPDFProcessClient(config) {
    override val promptVersion: String = "v1"
    override val promptFileName: String = "patient_leaflet"
    override val modelId: String = "gemini-flash-latest"

    override fun getUserFailureGenericMessage(): String {
        return "Não foi possível extrair as informações da bula para pacientes. Se o erro persistir, contate o suporte"
    }

    override fun getResponseSchema(): Schema {
        return PatientLeafletSchemaFactory.createSchema()
    }
}