package br.com.buscamed.domain.usecase

import br.com.buscamed.data.client.gemini.image.GeminiPillPackImageProcessClient
import br.com.buscamed.data.client.storage.google.image.PillPackGoogleStorageClient
import br.com.buscamed.domain.repository.LLMExecutionHistoryRepository

class ProcessPillPackImageUseCase(
    executionHistoryRepository: LLMExecutionHistoryRepository,
    geminiClient: GeminiPillPackImageProcessClient,
    storageClient: PillPackGoogleStorageClient
): BaseProcessImageUseCase(executionHistoryRepository, geminiClient, storageClient)