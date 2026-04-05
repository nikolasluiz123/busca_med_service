package br.com.buscamed.data.client.gemini.image

import br.com.buscamed.core.config.properties.GeminiConfig
import br.com.buscamed.data.client.gemini.core.client.GeminiImageProcessClient
import br.com.buscamed.data.client.gemini.core.schema.MedicalPrescriptionSchemaFactory
import com.google.genai.types.Schema

class GeminiMedicalPrescriptionImageProcessClient(config: GeminiConfig): GeminiImageProcessClient(config) {
    override val promptVersion: String = "v2"
    override val promptFileName: String = "medical_prescription"

    override fun getUserFailureGenericMessage(): String {
        return "Não foi possível extrair as informações da imagem da sua prescrição médica. Se o erro persistir, contate o suporte"
    }

    override fun getResponseSchema(): Schema {
        return MedicalPrescriptionSchemaFactory.createSchema()
    }
}