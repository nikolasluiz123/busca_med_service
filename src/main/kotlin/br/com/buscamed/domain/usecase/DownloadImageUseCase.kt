package br.com.buscamed.domain.usecase

import br.com.buscamed.core.enumeration.SupportedImageFormat
import br.com.buscamed.domain.exceptions.BusinessException
import br.com.buscamed.domain.exceptions.ResourceNotFoundException
import br.com.buscamed.domain.repository.LLMExecutionHistoryRepository
import br.com.buscamed.domain.service.ImageStorageService

/**
 * Caso de uso para realizar o download de uma imagem associada a um histórico de execução de LLM.
 *
 * @property repository O repositório para acessar o histórico de execuções.
 * @property storageService O serviço de armazenamento para baixar a imagem.
 * @property getSupportedImageFormatUseCase O caso de uso para obter o formato de imagem suportado.
 */
class DownloadImageUseCase(
    private val repository: LLMExecutionHistoryRepository,
    private val storageService: ImageStorageService,
    private val getSupportedImageFormatUseCase: GetSupportedImageFormatUseCase
) {
    /**
     * Executa o caso de uso para baixar a imagem.
     *
     * @param executionId O ID do histórico de execução.
     * @return Um [Pair] contendo o [ByteArray] da imagem e o [SupportedImageFormat] correspondente,
     * ou um par de nulos se o histórico não tiver uma imagem associada.
     * @throws BusinessException Se o `executionId` for nulo ou vazio.
     * @throws ResourceNotFoundException Se o histórico não for encontrado.
     */
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
