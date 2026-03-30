package br.com.buscamed.domain.model.system

import java.time.Instant

/**
 * Entidade de domínio que representa o estado do último processamento de uma rotina de sistema.
 *
 * @property id O identificador único do processo (ex: "ANVISA_MEDICATION_IMPORT").
 * @property lastHash O hash do último arquivo processado, para controle de alterações.
 * @property lastStorageFilePath O caminho no serviço de armazenamento do último arquivo salvo.
 * @property lastUpdated A data e hora da última atualização bem-sucedida do processo.
 */
data class SystemProcessControl(
    val id: String,
    val lastHash: String,
    val lastStorageFilePath: String,
    val lastUpdated: Instant
)
