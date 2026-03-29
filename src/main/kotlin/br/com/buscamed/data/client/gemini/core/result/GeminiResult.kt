package br.com.buscamed.data.client.gemini.core.result

import kotlinx.serialization.json.JsonObject

data class GeminiResult(
    val json: JsonObject,
    val inputTokens: Int,
    val outputTokens: Int,
    val promptFileName: String
)
