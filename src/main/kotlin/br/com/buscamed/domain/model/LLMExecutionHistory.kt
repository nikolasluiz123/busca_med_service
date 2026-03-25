package br.com.buscamed.domain.model

import java.time.Instant

data class LLMExecutionHistory(
    val inputTokens: Int,
    val outputTokens: Int,
    val result: String?,
    val success: Boolean,
    val startDate: Instant,
    val endDate: Instant,
    val id: String? = null,
    val storageImagePath: String? = null
)
