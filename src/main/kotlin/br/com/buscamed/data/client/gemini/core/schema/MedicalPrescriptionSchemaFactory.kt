package br.com.buscamed.data.client.gemini.core.schema

import br.com.buscamed.data.client.gemini.core.extensions.*
import com.google.genai.types.Schema

/**
 * Factory responsável por prover o schema de resposta estruturado para extração
 * de dados de prescrições médicas.
 */
object MedicalPrescriptionSchemaFactory {

    private val unidadesApresentacao = listOf("Miligrama", "Grama", "Mililitro", "Gota", "Unidade Internacional")
    private val unidadesDose = listOf("Comprimido", "Cápsula", "Gota", "Mililitro", "Colher", "Jato", "Sachê")
    private val unidadesFrequencia = listOf("Hora", "Dia", "Semana", "Mês")
    private val unidadesDuracao = listOf("Dia", "Semana", "Mês", "Ano")
    private val unidadesQtd = listOf("Caixa", "Frasco", "Ampola", "Tubo", "Comprimido", "Cápsula", "Sachê")

    /**
     * Constrói e retorna o schema esperado pela LLM.
     *
     * @return Instância de [Schema] configurada.
     */
    fun createSchema(): Schema {
        val apresentacaoDosagemSchema = Schema.builder().obj(
            mapOf(
                "valor" to Schema.builder().number(nullable = true),
                "unidade" to Schema.builder().string(nullable = true, enums = unidadesApresentacao)
            )
        )

        val doseSchema = Schema.builder().obj(
            mapOf(
                "valor" to Schema.builder().number(nullable = true),
                "unidade" to Schema.builder().string(nullable = true, enums = unidadesDose)
            )
        )

        val frequenciaSchema = Schema.builder().obj(
            mapOf(
                "intervalo" to Schema.builder().number(nullable = true),
                "unidade" to Schema.builder().string(nullable = true, enums = unidadesFrequencia),
                "texto_orientacao" to Schema.builder().string(nullable = true)
            )
        )

        val duracaoSchema = Schema.builder().obj(
            mapOf(
                "valor" to Schema.builder().number(nullable = true),
                "unidade" to Schema.builder().string(nullable = true, enums = unidadesDuracao),
                "uso_continuo" to Schema.builder().boolean()
            )
        )

        val qtdSchema = Schema.builder().obj(
            mapOf(
                "valor" to Schema.builder().number(nullable = true),
                "unidade" to Schema.builder().string(nullable = true, enums = unidadesQtd)
            )
        )

        val medicamentoSchema = Schema.builder().obj(
            mapOf(
                "nome" to Schema.builder().string(nullable = true),
                "apresentacao_dosagem" to apresentacaoDosagemSchema,
                "dose" to doseSchema,
                "frequencia" to frequenciaSchema,
                "duracao" to duracaoSchema,
                "quantidade_total_prescrita" to qtdSchema
            )
        )

        return Schema.builder().obj(
            mapOf(
                "medicamentos" to Schema.builder().array(items = medicamentoSchema)
            )
        )
    }
}