package br.com.buscamed.data.client.anvisa.exception

import br.com.buscamed.data.client.core.exception.IntegrationException
import io.ktor.http.HttpStatusCode

/**
 * Exceção lançada em cenários de falha na comunicação ou processamento
 * de dados provenientes dos serviços da ANVISA.
 *
 * @param technicalMessage Detalhes técnicos sobre a causa da falha.
 * @param statusCode O código HTTP representativo do erro. Padrão é [HttpStatusCode.BadGateway].
 * @param cause A exceção raiz que originou a falha, caso exista.
 */
class AnvisaIntegrationException(
    technicalMessage: String,
    statusCode: HttpStatusCode = HttpStatusCode.BadGateway,
    cause: Throwable? = null
) : IntegrationException(
    serviceName = "AnvisaIntegrationClient",
    technicalMessage = technicalMessage,
    userMessage = "Ocorreu uma instabilidade ao consultar a base de dados da ANVISA. Tente novamente mais tarde.",
    statusCode = statusCode,
    errorCode = "ANVISA_INTEGRATION_ERROR",
    cause = cause
)