package br.com.buscamed.data.repository

import br.com.buscamed.data.datasource.interfaces.LLMExecutionHistoryDataSource

class PatientLeafletExecutionHistoryRepositoryImpl(
    dataSource: LLMExecutionHistoryDataSource
): BaseLLMExecutionHistoryRepositoryImpl(dataSource)
