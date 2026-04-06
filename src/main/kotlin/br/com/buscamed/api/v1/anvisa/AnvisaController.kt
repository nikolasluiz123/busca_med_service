package br.com.buscamed.api.v1.anvisa

import br.com.buscamed.domain.usecase.ImportAnvisaInformationUseCase
import br.com.buscamed.domain.usecase.ImportAnvisaLeafletsUseCase
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond

/**
 * Controlador responsável por orquestrar as requisições relacionadas aos dados fornecidos pela ANVISA.
 *
 * @property importAnvisaInformationUseCase Caso de uso para importação e atualização de medicamentos da ANVISA.
 */
class AnvisaController(
    private val importAnvisaInformationUseCase: ImportAnvisaInformationUseCase,
    private val importAnvisaLeafletsUseCase: ImportAnvisaLeafletsUseCase
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

    /**
     * Processa a requisição para iniciar a rotina de download de bulas em PDF.
     */
    suspend fun importLeaflets(call: ApplicationCall) {
        importAnvisaLeafletsUseCase()
        call.respond(HttpStatusCode.Accepted)
    }
}