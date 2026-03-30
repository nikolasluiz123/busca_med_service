package br.com.buscamed.domain.exceptions

import br.com.buscamed.core.config.security.exeption.ServiceErrorCodes.BUSINESS_ERROR

/**
 * Exceção lançada quando uma regra de negócio da aplicação é violada.
 *
 * Representa um erro que impede a continuação de um fluxo devido a uma condição
 * de negócio inválida, retornando um status HTTP 400 (Bad Request) por padrão.
 *
 * @param message A mensagem descrevendo a regra de negócio que foi violada.
 * @param errorCode O código de erro específico para a regra de negócio,
 *                  sendo o padrão [BUSINESS_ERROR].
 */
open class BusinessException(
    message: String,
    errorCode: String = BUSINESS_ERROR
) : BuscaMedException(message, 400, errorCode)
