package br.com.buscamed.data.client.storage.google.core

import br.com.buscamed.data.client.storage.core.StorageClient
import com.google.cloud.storage.Storage
import com.google.cloud.storage.StorageOptions
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.FileNotFoundException

abstract class GoogleStorageClient: StorageClient() {
    protected val logger: Logger = LoggerFactory.getLogger(this::class.java)
    protected val storage: Storage = StorageOptions.getDefaultInstance().service

    protected abstract val bucketName: String

    override suspend fun download(fileName: String): ByteArray {
        val blob = storage.get(bucketName, fileName) ?: throw FileNotFoundException("File $fileName not found in $bucketName")
        return blob.getContent()
    }
}