package br.com.buscamed.data.client.gemini.image

import br.com.buscamed.core.config.properties.GeminiConfig
import br.com.buscamed.data.client.gemini.core.client.GeminiImageProcessClient

class GeminiMedicalPrescriptionImageProcessClient(config: GeminiConfig): GeminiImageProcessClient(config) {
    override val promptVersion: String = "v1"
    override val promptFileName: String = "medical_prescription"

    override fun getUserFailureGenericMessage(): String {
        return "Não foi possível extrair as informações da imagem da sua prescrição médica. Se o erro persistir, contate o suporte"
    }
}