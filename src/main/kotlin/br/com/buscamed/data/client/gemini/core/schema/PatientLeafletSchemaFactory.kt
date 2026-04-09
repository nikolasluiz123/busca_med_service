package br.com.buscamed.data.client.gemini.core.schema

import br.com.buscamed.data.client.gemini.core.extensions.*
import com.google.genai.types.Schema

/**
 * Factory responsável por prover o schema de resposta estruturado para extração
 * de dados de bulas direcionadas a pacientes leigos.
 */
object PatientLeafletSchemaFactory {

    /**
     * Constrói e retorna o schema esperado pela LLM.
     *
     * @return Instância de [Schema] configurada.
     */
    fun createSchema(): Schema {
        return Schema.builder().obj(
            mapOf(
                "indications" to Schema.builder().array(items = Schema.builder().string()),
                "mechanism_of_action" to Schema.builder().string(),
                "contraindications" to Schema.builder().array(items = Schema.builder().string()),
                "precautions_and_warnings" to Schema.builder().array(items = Schema.builder().string()),
                "interactions_to_avoid" to Schema.builder().array(items = Schema.builder().string()),
                "how_to_use" to Schema.builder().string(),
                "missed_dose" to Schema.builder().string(),
                "common_side_effects" to Schema.builder().array(items = Schema.builder().string())
            )
        )
    }
}