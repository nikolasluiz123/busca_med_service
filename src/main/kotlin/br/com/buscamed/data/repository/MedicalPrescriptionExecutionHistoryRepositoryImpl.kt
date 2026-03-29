package br.com.buscamed.data.repository

import br.com.buscamed.data.datasource.interfaces.LLMExecutionHistoryDataSource

class MedicalPrescriptionExecutionHistoryRepositoryImpl(
    dataSource: LLMExecutionHistoryDataSource
): BaseLLMExecutionHistoryRepositoryImpl(dataSource)