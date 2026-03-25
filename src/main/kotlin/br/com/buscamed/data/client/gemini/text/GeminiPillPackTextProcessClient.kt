package br.com.buscamed.data.client.gemini.text

import br.com.buscamed.data.client.gemini.core.client.GeminiTextProcessClient
import io.ktor.server.application.ApplicationEnvironment

class GeminiPillPackTextProcessClient(environment: ApplicationEnvironment): GeminiTextProcessClient(environment) {
    override val promptVersion: String = "v1"
    override val promptFileName: String = "pill_pack"

    override fun getUserFailureGenericMessage(): String {
        return "Não foi possível extrair as informações da imagem da sua cartela de comprimidos. Se o erro persistir, contate o suporte"
    }
}