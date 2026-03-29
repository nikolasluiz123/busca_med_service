package br.com.buscamed.data.client.storage.google.image

import br.com.buscamed.data.client.storage.google.core.ImagesGoogleStorageClient
import com.google.cloud.storage.Storage

class MedicalPrescriptionGoogleStorageClient(storage: Storage): ImagesGoogleStorageClient(storage) {
    override val directory: String = "medical_prescription"
}