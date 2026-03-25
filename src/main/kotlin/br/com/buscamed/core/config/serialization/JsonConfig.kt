package br.com.buscamed.core.config.serialization

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonBuilder

/**
 * Cria uma instância de JSON com os padrões, permitindo personalização.
 *
 * @param extraConfig Bloco opcional para adicionar ou sobrescrever configurações.
 */
fun createJson(extraConfig: JsonBuilder.() -> Unit = {}): Json {
    return Json {
        ignoreUnknownKeys = true
        prettyPrint = true
        isLenient = true
        encodeDefaults = true

        extraConfig()
    }
}

/**
 * Instância padrão para uso geral onde não é necessária personalização extra.
 */
val DefaultJson = createJson()