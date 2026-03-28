package br.com.buscamed.domain.model.system

import java.time.Instant

/**
 * Entidade de domínio que representa o estado do último processamento de uma rotina de sistema.
 */
data class SystemProcessControl(
    val id: String,
    val lastHash: String,
    val lastStorageFilePath: String,
    val lastUpdated: Instant
)