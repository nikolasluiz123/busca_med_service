package br.com.buscamed.domain.repository

import br.com.buscamed.domain.model.system.SystemProcessControl

/**
 * Contrato de repositório para o controle de execuções de processos sistêmicos
 * em tarefas de segundo plano (como cron jobs ou tarefas assíncronas agendadas).
 */
interface SystemProcessControlRepository {

    /**
     * Busca um registro de controle de processo através de seu identificador único.
     *
     * @param id O identificador que representa o tipo de processo.
     * @return A entidade [SystemProcessControl] caso exista registro de execução.
     */
    suspend fun findById(id: String): SystemProcessControl?

    /**
     * Salva ou atualiza um registro contendo os dados e status atualizado do processo do sistema.
     *
     * @param control O objeto de [SystemProcessControl] para persistência.
     */
    suspend fun save(control: SystemProcessControl)
}
