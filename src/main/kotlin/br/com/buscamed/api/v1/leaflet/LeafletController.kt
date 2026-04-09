package br.com.buscamed.api.v1.leaflet

import br.com.buscamed.api.v1.dto.response.PatientLeafletResponseDTO
import br.com.buscamed.api.v1.dto.response.ProfessionalLeafletResponseDTO
import br.com.buscamed.core.config.serialization.DefaultJson
import br.com.buscamed.domain.exceptions.BusinessException
import br.com.buscamed.domain.usecase.ResumeLeafletUseCase
import br.com.buscamed.domain.usecase.ResumePatientLeafletUseCase
import br.com.buscamed.domain.usecase.ResumeProfessionalLeafletUseCase
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.response.respond

/**
 * Controlador responsável por orquestrar as requisições relacionadas a bulas de medicamentos (Leaflet).
 */
class LeafletController(
    private val resumeLeafletUseCase: ResumeLeafletUseCase,
    private val resumePatientLeafletUseCase: ResumePatientLeafletUseCase,
    private val resumeProfessionalLeafletUseCase: ResumeProfessionalLeafletUseCase
) {

    /**
     * Inicia a rotina de resumo de bulas (ambos pacientes e profissionais).
     *
     * @param call O contexto da requisição Ktor.
     */
    suspend fun resume(call: ApplicationCall) {
        resumeLeafletUseCase()
        call.respond(HttpStatusCode.Accepted)
    }

    /**
     * Resume uma bula de paciente para um medicamento específico.
     *
     * @param call O contexto da requisição Ktor.
     */
    suspend fun resumePatient(call: ApplicationCall) {
        val medicationId = call.request.queryParameters["medicationId"]
            ?: throw BusinessException("O parâmetro 'medicationId' é obrigatório.")

        val leaflet = resumePatientLeafletUseCase(medicationId = medicationId, autoSave = true)
        val resumeString = leaflet?.leafletResume

        if (resumeString != null) {
            val dto = DefaultJson.decodeFromString(PatientLeafletResponseDTO.serializer(), resumeString)
            call.respond(HttpStatusCode.OK, dto)
        } else {
            call.respond(HttpStatusCode.NoContent)
        }
    }

    /**
     * Resume uma bula de profissional para um medicamento específico.
     *
     * @param call O contexto da requisição Ktor.
     */
    suspend fun resumeProfessional(call: ApplicationCall) {
        val medicationId = call.request.queryParameters["medicationId"]
            ?: throw BusinessException("O parâmetro 'medicationId' é obrigatório.")

        val leaflet = resumeProfessionalLeafletUseCase(medicationId = medicationId, autoSave = true)
        val resumeString = leaflet?.leafletResume

        if (resumeString != null) {
            val dto = DefaultJson.decodeFromString(ProfessionalLeafletResponseDTO.serializer(), resumeString)
            call.respond(HttpStatusCode.OK, dto)
        } else {
            call.respond(HttpStatusCode.NoContent)
        }
    }
}