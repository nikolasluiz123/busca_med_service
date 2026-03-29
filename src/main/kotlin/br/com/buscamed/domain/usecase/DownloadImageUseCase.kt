package br.com.buscamed.domain.usecase

import br.com.buscamed.core.enumeration.SupportedImageFormat
import br.com.buscamed.domain.exceptions.BusinessException
import br.com.buscamed.domain.exceptions.ResourceNotFoundException
import br.com.buscamed.domain.repository.LLMExecutionHistoryRepository
import br.com.buscamed.domain.service.ImageStorageService

class DownloadImageUseCase(
    private val repository: LLMExecutionHistoryRepository,
    private val storageService: ImageStorageService,
    private val getSupportedImageFormatUseCase: GetSupportedImageFormatUseCase
) {
    suspend operator fun invoke(executionId: String?): Pair<ByteArray?, SupportedImageFormat?> {
        if (executionId.isNullOrBlank()) {
            throw BusinessException("O parâmetro 'executionId' é obrigatório.")
        }

        val history = repository.findHistoryById(executionId)
            ?: throw ResourceNotFoundException("Histórico não encontrado para o executionId: $executionId")

        val storageImagePath = history.storageImagePath ?: return Pair(null, null)

        val fileName = storageImagePath.substringAfter("gs://").substringAfter("/")
        val extension = fileName.substringAfterLast('.', "")
        val format = getSupportedImageFormatUseCase(extension)

        return Pair(storageService.download(fileName), format)
    }
}