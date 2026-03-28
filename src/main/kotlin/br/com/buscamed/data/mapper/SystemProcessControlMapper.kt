package br.com.buscamed.data.mapper

import br.com.buscamed.data.document.SystemProcessControlDocument
import br.com.buscamed.domain.model.system.SystemProcessControl

fun SystemProcessControl.toDocument(): SystemProcessControlDocument {
    return SystemProcessControlDocument(
        id = this.id,
        lastHash = this.lastHash,
        lastStorageFilePath = this.lastStorageFilePath,
        lastUpdated = this.lastUpdated
    )
}

fun SystemProcessControlDocument.toDomain(): SystemProcessControl {
    return SystemProcessControl(
        id = this.id ?: "",
        lastHash = this.lastHash,
        lastStorageFilePath = this.lastStorageFilePath,
        lastUpdated = this.lastUpdated
    )
}