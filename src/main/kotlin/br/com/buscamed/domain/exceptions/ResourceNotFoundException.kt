package br.com.buscamed.domain.exceptions

import br.com.buscamed.core.config.security.exeption.ServiceErrorCodes.RESOURCE_NOT_FOUND

open class ResourceNotFoundException(
    message: String,
    errorCode: String = RESOURCE_NOT_FOUND
) : BuscaMedException(message, 404, errorCode)