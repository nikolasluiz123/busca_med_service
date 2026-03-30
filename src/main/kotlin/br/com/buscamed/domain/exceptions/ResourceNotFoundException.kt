package br.com.buscamed.domain.exceptions

import br.com.buscamed.core.config.security.exeption.ServiceErrorCodes.RESOURCE_NOT_FOUND

/**
 * Exceção lançada quando um recurso solicitado não pode ser encontrado no sistema.
 *
 * Indica que uma entidade ou dado específico, como um registro no banco de dados
 * ou um arquivo, não existe, resultando em um status HTTP 404 (Not Found).
 *
 * @param message A mensagem detalhando qual recurso não foi encontrado.
 * @param errorCode O código de erro, que por padrão é [RESOURCE_NOT_FOUND].
 */
open class ResourceNotFoundException(
    message: String,
    errorCode: String = RESOURCE_NOT_FOUND
) : BuscaMedException(message, 404, errorCode)
