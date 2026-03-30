package br.com.buscamed.data.client.storage.google.core

import br.com.buscamed.core.utils.ImageFileUtils
import br.com.buscamed.domain.service.ImageStorageService
import com.google.cloud.storage.BlobId
import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.Storage

/**
 * Cliente Google Storage focado no salvamento de imagens.
 *
 * Define um bucket padrão para imagens processadas e implementa o upload dinâmico
 * onde o caminho gerado combina o diretório configurado pelo filho e data da submissão.
 *
 * @param storage A instância do serviço de Storage do Google Cloud.
 */
abstract class ImagesGoogleStorageClient(storage: Storage): GoogleStorageClient(storage), ImageStorageService {

    override val bucketName: String = "gemini_processed_images"

    /** Diretório raiz dentro do bucket de imagens específico para o tipo tratado pelo cliente. */
    abstract val directory: String

    override suspend fun upload(content: ByteArray, mimeType: String): String {
        val extension = ImageFileUtils.getExtensionFromMimeType(mimeType)
        val fileName = "$directory/${getDefaultFileName()}.$extension"

        val blobId = BlobId.of(bucketName, fileName)
        val blobInfo = BlobInfo.newBuilder(blobId)
            .setContentType(mimeType)
            .build()

        logger.info("Uploaded image: gs://$bucketName/$fileName")

        storage.create(blobInfo, content)

        return "gs://$bucketName/$fileName"
    }
}
