package br.com.buscamed.domain.exceptions

import br.com.buscamed.core.config.security.exeption.ServiceErrorCodes.BUSINESS_ERROR
import io.ktor.http.HttpStatusCode

open class BusinessException(
    message: String,
    errorCode: String = BUSINESS_ERROR
) : BuscaMedException(message, HttpStatusCode.BadRequest, errorCode)