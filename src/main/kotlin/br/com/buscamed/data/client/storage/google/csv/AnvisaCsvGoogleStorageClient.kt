package br.com.buscamed.data.client.storage.google.csv

import br.com.buscamed.data.client.storage.google.core.GoogleStorageClient
import com.google.cloud.storage.BlobId
import com.google.cloud.storage.BlobInfo

/**
 * Cliente de armazenamento responsável pelo envio de arquivos CSV da ANVISA para o Google Cloud Storage.
 */
class AnvisaCsvGoogleStorageClient : GoogleStorageClient() {

    override val bucketName: String = "anvisa_csv_files"

    /**
     * Realiza o upload do conteúdo binário do arquivo para o bucket configurado.
     *
     * @param content O conteúdo do arquivo em formato de array de bytes.
     * @param mimeType O tipo MIME do conteúdo, esperado como "text/csv".
     * @return O caminho absoluto gerado para o arquivo no formato gs://.
     */
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