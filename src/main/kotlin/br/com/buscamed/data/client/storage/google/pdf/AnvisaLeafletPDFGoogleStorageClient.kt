package br.com.buscamed.data.client.storage.google.pdf

import br.com.buscamed.data.client.storage.google.core.GoogleStorageClient
import br.com.buscamed.domain.service.LeafletStorageService
import com.google.cloud.storage.BlobId
import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.Storage
import org.slf4j.LoggerFactory

class AnvisaLeafletPDFGoogleStorageClient(storage: Storage) : GoogleStorageClient(storage), LeafletStorageService {
    override val bucketName: String = "anvisa_pdf_files"

    override suspend fun upload(medicationId: String, fileId: String, content: ByteArray): String {
        val path = "leaflet/$medicationId/$fileId.pdf"
        val blobId = BlobId.of(bucketName, path)
        val blobInfo = BlobInfo.newBuilder(blobId).setContentType("application/pdf").build()

        logger.info("Uploaded PDF: $path")

        storage.create(blobInfo, content)
        return "gs://$bucketName/$path"
    }

    override suspend fun upload(content: ByteArray, mimeType: String): String {
        throw NotImplementedError("Utilize o método upload(medicationId: String, fileId: String, content: ByteArray) em vez deste método.")
    }
}