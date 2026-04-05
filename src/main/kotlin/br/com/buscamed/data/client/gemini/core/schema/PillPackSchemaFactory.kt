package br.com.buscamed.data.client.gemini.core.schema

import br.com.buscamed.data.client.gemini.core.extensions.*
import com.google.genai.types.Schema

/**
 * Factory responsável por prover o schema de resposta estruturado para extração
 * de dados de cartelas de comprimidos (Pill Pack).
 */
object PillPackSchemaFactory {

    private val unidadesDosagem = listOf("Miligrama", "Grama", "Mililitro", "Unidade Internacional", "Micrograma")
    private val viasAdministracao = listOf("Uso oral", "Uso sublingual", "Uso tópico", "Uso injetável")

    /**
     * Constrói e retorna o schema esperado pela LLM.
     *
     * @return Instância de [Schema] configurada.
     */
    fun createSchema(): Schema {
        val componenteSchema = Schema.builder().obj(
            mapOf(
                "principio_ativo" to Schema.builder().string(),
                "dosagem_valor" to Schema.builder().number(nullable = true),
                "dosagem_unidade" to Schema.builder().string(nullable = true, enums = unidadesDosagem)
            )
        )

        val usoSchema = Schema.builder().obj(
            mapOf(
                "vias_administracao" to Schema.builder().array(
                    items = Schema.builder().string(enums = viasAdministracao),
                    nullable = true
                ),
                "restricoes_idade" to Schema.builder().array(
                    items = Schema.builder().string(),
                    nullable = true
                )
            ),
            nullable = true
        )

        return Schema.builder().obj(
            mapOf(
                "nome_medicamento" to Schema.builder().string(nullable = true),
                "componentes" to Schema.builder().array(items = componenteSchema),
                "uso" to usoSchema,
                "indicacoes" to Schema.builder().array(items = Schema.builder().string(), nullable = true),
                "data_validade" to Schema.builder().string(nullable = true),
                "lote" to Schema.builder().string(nullable = true)
            )
        )
    }
}