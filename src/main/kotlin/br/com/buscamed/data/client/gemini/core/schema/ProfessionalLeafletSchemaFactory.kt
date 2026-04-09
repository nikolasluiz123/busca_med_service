package br.com.buscamed.data.client.gemini.core.schema

import br.com.buscamed.data.client.gemini.core.extensions.*
import com.google.genai.types.Schema

/**
 * Factory responsável por prover o schema de resposta estruturado para extração
 * de dados de bulas direcionadas a profissionais da saúde.
 */
object ProfessionalLeafletSchemaFactory {

    /**
     * Constrói e retorna o schema esperado pela LLM.
     *
     * @return Instância de [Schema] configurada.
     */
    fun createSchema(): Schema {
        return Schema.builder().obj(
            mapOf(
                "indications_and_spectrum" to Schema.builder().array(items = Schema.builder().string()),
                "pharmacological_properties" to Schema.builder().string(),
                "clinical_warnings" to Schema.builder().array(items = Schema.builder().string()),
                "dosage_adjustments" to Schema.builder().array(items = Schema.builder().string()),
                "drug_interactions" to Schema.builder().array(items = Schema.builder().string()),
                "adverse_reactions" to Schema.builder().array(items = Schema.builder().string()),
                "lab_test_interferences" to Schema.builder().array(items = Schema.builder().string())
            )
        )
    }
}