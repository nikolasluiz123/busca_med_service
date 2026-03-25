package br.com.buscamed.data.client.gemini.text

import br.com.buscamed.data.client.gemini.core.client.GeminiTextProcessClient
import io.ktor.server.application.ApplicationEnvironment

class GeminiMedicalPrescriptionTextProcessClient(environment: ApplicationEnvironment): GeminiTextProcessClient(environment) {
    override val promptVersion: String = "v1"
    override val promptFileName: String = "medical_prescription"

    override fun getUserFailureGenericMessage(): String {
        return "Não foi possível extrair as informações da imagem da sua prescrição médica. Se o erro persistir, contate o suporte"
    }
}