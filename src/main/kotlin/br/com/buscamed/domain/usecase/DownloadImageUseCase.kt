package br.com.buscamed.domain.usecase

import br.com.buscamed.data.client.storage.core.StorageClient
import br.com.buscamed.data.datasource.interfaces.LLMExecutionHistoryDataSource
import br.com.buscamed.domain.exceptions.BusinessException
import br.com.buscamed.domain.exceptions.ResourceNotFoundException
import io.ktor.http.ContentType

class DownloadImageUseCase(
    private val executionHistoryDataSource: LLMExecutionHistoryDataSource,
    private val storageClient: StorageClient,
    private val getContentTypeByExtensionUseCase: GetContentTypeByExtensionUseCase
) {
    suspend operator fun invoke(executionId: String?): Pair<ByteArray?, ContentType> {
        if (executionId.isNullOrBlank()) {
            throw BusinessException("O parâmetro 'executionId' é obrigatório.")
        }
        
        val history = executionHistoryDataSource.findHistoryById(executionId) 
            ?: throw ResourceNotFoundException("Histórico não encontrado para o executionId: $executionId")
        
        val storageImagePath = history.storageImagePath ?: return Pair(null, ContentType.Image.Any)
            
        val fileName = storageImagePath.substringAfter("gs://").substringAfter("/")

        val extension = fileName.substringAfterLast('.', "")
        val contentType = getContentTypeByExtensionUseCase(extension)

        return Pair(storageClient.download(fileName), contentType)
    }
}