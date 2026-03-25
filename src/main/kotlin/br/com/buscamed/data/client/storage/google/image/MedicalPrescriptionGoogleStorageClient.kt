package br.com.buscamed.data.client.storage.google.image

import br.com.buscamed.data.client.storage.google.core.ImagesGoogleStorageClient

class MedicalPrescriptionGoogleStorageClient: ImagesGoogleStorageClient() {
    override val directory: String = "medical_prescription"
}