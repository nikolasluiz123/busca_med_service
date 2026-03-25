package br.com.buscamed.domain.usecase

import br.com.buscamed.data.client.gemini.image.GeminiMedicalPrescriptionImageProcessClient
import br.com.buscamed.data.client.storage.google.image.MedicalPrescriptionGoogleStorageClient
import br.com.buscamed.domain.repository.LLMExecutionHistoryRepository

class ProcessMedicalPrescriptionImageUseCase(
    executionHistoryRepository: LLMExecutionHistoryRepository,
    geminiClient: GeminiMedicalPrescriptionImageProcessClient,
    storageClient: MedicalPrescriptionGoogleStorageClient
) : BaseProcessImageUseCase(executionHistoryRepository, geminiClient, storageClient)