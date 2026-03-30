package br.com.buscamed.data.client.core

import br.com.buscamed.core.config.serialization.DefaultJson
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.cio.CIOEngineConfig
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * Factory utilitária para criação de clientes HTTP padronizados usando o Ktor com CIO Engine.
 */
object HttpClientFactory {

    /**
     * Cria um HttpClient configurado com Engine CIO, ContentNegotiation e Timeouts.
     *
     * @param jsonInstance Instância do Json configurada (padrão: DefaultJson).
     * @param requestTimeoutMillis Timeout total da requisição (padrão: 10s).
     * @param connectTimeoutMillis Timeout de conexão (padrão: 5s).
     * @param extraConfig Bloco para configurações adicionais do Ktor Client.
     * @return Uma instância configurada de [HttpClient].
     */
    fun createClient(
        jsonInstance: Json = DefaultJson,
        requestTimeoutMillis: Long = 10_000,
        connectTimeoutMillis: Long = 5_000,
        extraConfig: HttpClientConfig<CIOEngineConfig>.() -> Unit = {}
    ): HttpClient {
        return HttpClient(CIO) {
            install(ContentNegotiation) {
                json(jsonInstance)
            }

            install(HttpTimeout) {
                this.requestTimeoutMillis = requestTimeoutMillis
                this.connectTimeoutMillis = connectTimeoutMillis
            }

            extraConfig()
        }
    }
}
