package br.com.buscamed.core.dto

import kotlinx.serialization.Serializable
import java.time.Instant

/**
 * Representa a estrutura padrão de resposta para quando a API retornar um erro (status HTTP >= 400).
 *
 * @property errorCode Um código interno de erro para ajudar clientes a tratarem cenários específicos (ex: "RESOURCE_NOT_FOUND").
 * @property message Uma mensagem descritiva do erro, muitas vezes adequada para exibição ao usuário final.
 * @property timestamp A data e hora (em formato ISO-8601) em que o erro ocorreu.
 * @property path O caminho da requisição HTTP (URI) que causou o erro.
 * @property traceId O identificador de rastreamento distribuído (como do GCP Cloud Trace), útil para debug e suporte.
 */
@Serializable
data class ErrorResponseDTO(
    val errorCode: String,
    val message: String,
    val timestamp: String = Instant.now().toString(),
    val path: String,
    val traceId: String? = null
)
