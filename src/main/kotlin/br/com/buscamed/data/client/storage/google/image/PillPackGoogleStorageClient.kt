package br.com.buscamed.data.client.storage.google.image

import br.com.buscamed.data.client.storage.google.core.ImagesGoogleStorageClient

class PillPackGoogleStorageClient: ImagesGoogleStorageClient() {
    override val directory: String = "pill_pack"
}