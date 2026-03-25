package br.com.buscamed.core.config.serialization

import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.contentnegotiation.ContentNegotiationConfig
import kotlinx.serialization.json.JsonBuilder

/**
 * Configura a serialização padrão para os microsserviços.
 *
 * @param extraJsonConfig Permite que o microsserviço sobrescreva ou adicione configurações ao JSON.
 * @param contentNegotiationBlock Permite adicionar outros conversores (ex: XML) além do JSON.
 */
fun Application.configureSerialization(
    extraJsonConfig: JsonBuilder.() -> Unit = {},
    contentNegotiationBlock: ContentNegotiationConfig.() -> Unit = {}
) {
    install(ContentNegotiation) {
        json(createJson(extraJsonConfig))
        contentNegotiationBlock()
    }
}