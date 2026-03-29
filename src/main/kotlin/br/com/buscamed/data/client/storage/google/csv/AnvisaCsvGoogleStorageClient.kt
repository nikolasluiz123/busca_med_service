package br.com.buscamed.data.client.storage.google.csv

import br.com.buscamed.data.client.storage.google.core.GoogleStorageClient
import br.com.buscamed.domain.service.CsvStorageService
import com.google.cloud.storage.BlobId
import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.Storage

class AnvisaCsvGoogleStorageClient(storage: Storage) : GoogleStorageClient(storage), CsvStorageService {

    override val bucketName: String = "anvisa_csv_files"

    override suspend fun upload(content: ByteArray, mimeType: String): String {
        val fileName = "anvisa_medicamentos_${getDefaultFileName()}.csv"

        val blobId = BlobId.of(bucketName, fileName)
        val blobInfo = BlobInfo.newBuilder(blobId)
            .setContentType(mimeType)
            .build()

        logger.info("Iniciando upload do arquivo CSV: gs://$bucketName/$fileName")

        storage.create(blobInfo, content)

        return "gs://$bucketName/$fileName"
    }
}