package br.com.buscamed.data.client.gemini.core.client

import br.com.buscamed.core.config.properties.GeminiConfig
import br.com.buscamed.data.client.gemini.core.exception.GeminiErrorCodes
import br.com.buscamed.data.client.gemini.core.exception.GeminiIntegrationException
import com.google.genai.types.Candidate
import com.google.genai.types.Content
import com.google.genai.types.GenerateContentResponse
import com.google.genai.types.GenerateContentResponseUsageMetadata
import com.google.genai.types.Part
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

/**
 * Unit tests for [GeminiProcessClient].
 */
class GeminiProcessClientTest {

    companion object {
        private const val EXPECTED_PROMPT_NAME = "fake_prompt_v1"
        private const val GENERIC_ERROR_MESSAGE = "Generic Failure"
        private const val INPUT_TOKENS = 15
        private const val OUTPUT_TOKENS = 45

        private val VALID_PRESCRIPTION_JSON = """
            {
              "medicamentos": [
                {
                  "nome": "Paracetamol",
                  "apresentacao_dosagem": { "valor": 500, "unidade": "mg" },
                  "frequencia": { "intervalo": 8, "unidade": "horas", "texto_orientacao": null }
                }
              ]
            }
        """.trimIndent()

        private val VALID_PILL_PACK_JSON = """
            {
              "nome_medicamento": "Aspirina",
              "componentes": [
                { "principio_ativo": "Ácido Acetilsalicílico", "dosagem_valor": 500, "dosagem_unidade": "mg" }
              ],
              "uso": { "vias_administracao": ["Uso oral"], "restricoes_idade": [] },
              "indicacoes": ["Dor de cabeça"],
              "data_validade": "10/25",
              "lote": "L1234"
            }
        """.trimIndent()

        private const val INVALID_JSON_ARRAY = """[{"medicamento": "Paracetamol"}]"""
        private const val INVALID_JSON_PRIMITIVE = """"Apenas um texto de resposta""""
        private const val MALFORMED_JSON = """{"medicamentos": """
    }

    private val testClient = object : GeminiProcessClient(GeminiConfig("fake-project", "fake-location")) {
        override val modelId: String = "fake-model"
        override val promptVersion: String = "v1"
        override val promptsDirectoryName: String = "fake_dir"
        override val promptFileName: String = "fake_prompt"

        override fun getUserFailureGenericMessage(): String = GENERIC_ERROR_MESSAGE

        fun exposeProcessResponse(response: GenerateContentResponse) = processResponse(response)
    }

    @Test
    fun processResponse_validPrescriptionJson_returnsProcessResult() {
        val mockResponse = createMockGenerateContentResponse(VALID_PRESCRIPTION_JSON, INPUT_TOKENS, OUTPUT_TOKENS)

        val result = testClient.exposeProcessResponse(mockResponse)

        val expectedCleanJson = Json.parseToJsonElement(VALID_PRESCRIPTION_JSON).toString()

        assertEquals(expectedCleanJson, result.resultText)
        assertEquals(INPUT_TOKENS, result.inputTokens)
        assertEquals(OUTPUT_TOKENS, result.outputTokens)
        assertEquals(EXPECTED_PROMPT_NAME, result.promptName)
    }

    @Test
    fun processResponse_validPillPackJson_returnsProcessResult() {
        val mockResponse = createMockGenerateContentResponse(VALID_PILL_PACK_JSON, INPUT_TOKENS, OUTPUT_TOKENS)

        val result = testClient.exposeProcessResponse(mockResponse)
        val expectedCleanJson = Json.parseToJsonElement(VALID_PILL_PACK_JSON).toString()

        assertEquals(expectedCleanJson, result.resultText)
        assertEquals(INPUT_TOKENS, result.inputTokens)
        assertEquals(OUTPUT_TOKENS, result.outputTokens)
    }

    @Test
    fun processResponse_jsonArray_throwsGeminiIntegrationException() {
        val mockResponse = createMockGenerateContentResponse(INVALID_JSON_ARRAY, INPUT_TOKENS, OUTPUT_TOKENS)

        val exception = assertThrows<GeminiIntegrationException> {
            testClient.exposeProcessResponse(mockResponse)
        }

        assertEquals(GeminiErrorCodes.GOOGLE_GEMINI_MALFORMED_RESULT, exception.errorCode)
        assertEquals(GENERIC_ERROR_MESSAGE, exception.userMessage)
    }

    @Test
    fun processResponse_jsonPrimitive_throwsGeminiIntegrationException() {
        val mockResponse = createMockGenerateContentResponse(INVALID_JSON_PRIMITIVE, INPUT_TOKENS, OUTPUT_TOKENS)

        val exception = assertThrows<GeminiIntegrationException> {
            testClient.exposeProcessResponse(mockResponse)
        }

        assertEquals(GeminiErrorCodes.GOOGLE_GEMINI_MALFORMED_RESULT, exception.errorCode)
        assertEquals(GENERIC_ERROR_MESSAGE, exception.userMessage)
    }

    @Test
    fun processResponse_malformedJson_throwsGeminiIntegrationException() {
        val mockResponse = createMockGenerateContentResponse(MALFORMED_JSON, INPUT_TOKENS, OUTPUT_TOKENS)

        val exception = assertThrows<GeminiIntegrationException> {
            testClient.exposeProcessResponse(mockResponse)
        }

        assertEquals(GeminiErrorCodes.GOOGLE_GEMINI_MALFORMED_RESULT, exception.errorCode)
        assertEquals(GENERIC_ERROR_MESSAGE, exception.userMessage)
    }

    private fun createMockGenerateContentResponse(
        textContent: String,
        promptTokens: Int,
        candidatesTokens: Int
    ): GenerateContentResponse {
        val part = Part.builder().text(textContent).build()
        val content = Content.builder().parts(listOf(part)).build()
        val candidate = Candidate.builder().content(content).build()

        val usageMetadata = GenerateContentResponseUsageMetadata.builder()
            .promptTokenCount(promptTokens)
            .candidatesTokenCount(candidatesTokens)
            .build()

        return GenerateContentResponse.builder()
            .candidates(listOf(candidate))
            .usageMetadata(usageMetadata)
            .build()
    }
}