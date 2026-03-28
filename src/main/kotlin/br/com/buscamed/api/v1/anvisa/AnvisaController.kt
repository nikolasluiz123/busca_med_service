package br.com.buscamed.api.v1.anvisa

import br.com.buscamed.domain.usecase.ImportAnvisaInformationUseCase
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond

/**
 * Controlador responsável por orquestrar as requisições relacionadas aos dados fornecidos pela ANVISA.
 *
 * @property importAnvisaInformationUseCase Caso de uso para importação e atualização de medicamentos da ANVISA.
 */
class AnvisaController(
    private val importAnvisaInformationUseCase: ImportAnvisaInformationUseCase
) {

    /**
     * Processa a requisição para iniciar a rotina de sincronização de medicamentos da ANVISA.
     *
     * @param call O contexto da requisição Ktor.
     */
    suspend fun importInformation(call: ApplicationCall) {
        importAnvisaInformationUseCase()
        call.respond(HttpStatusCode.Accepted)
    }
}