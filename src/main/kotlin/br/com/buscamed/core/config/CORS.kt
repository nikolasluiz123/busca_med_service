package br.com.buscamed.core.config

import io.ktor.http.*
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.cors.routing.*

/**
 * Configura as políticas de CORS (Cross-Origin Resource Sharing) para a aplicação Ktor.
 *
 * Por padrão, permite requisições de qualquer origem (`anyHost`) e qualquer método HTTP (`anyMethod`),
 * o que é adequado para desenvolvimento ou APIs públicas. Em ambientes de produção mais restritos,
 * estas configurações devem ser ajustadas para domínios específicos.
 *
 * Headers essenciais como Authorization e Content-Type são explicitamente permitidos, assim como o uso
 * de credenciais.
 */
fun Application.configureCORS() {
    install(CORS) {
        anyHost()
        anyMethod()

        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.ContentType)

        allowCredentials = true
    }
}
