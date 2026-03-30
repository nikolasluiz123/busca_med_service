package br.com.buscamed.data.client.storage.google.core

import br.com.buscamed.data.client.storage.core.StorageClient
import com.google.cloud.storage.Storage
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.FileNotFoundException

/**
 * Classe base para serviços de armazenamento que utilizam o Google Cloud Storage (GCS).
 * Realiza as operações básicas implementando a infraestrutura da SDK do Google.
 *
 * @property storage Cliente GCS oficial instanciado pelo SDK.
 */
abstract class GoogleStorageClient(protected val storage: Storage): StorageClient() {
    protected val logger: Logger = LoggerFactory.getLogger(this::class.java)

    /** O nome do bucket a ser utilizado nas operações deste cliente. */
    protected abstract val bucketName: String

    override suspend fun download(fileName: String): ByteArray {
        val blob = storage.get(bucketName, fileName) ?: throw FileNotFoundException("File $fileName not found in $bucketName")
        return blob.getContent()
    }
}
