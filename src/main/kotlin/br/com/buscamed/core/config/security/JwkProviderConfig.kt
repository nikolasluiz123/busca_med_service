package br.com.buscamed.core.config.security

import java.net.URI
import java.net.URL
import java.util.concurrent.TimeUnit

/**
 * Define as configurações de performance e resiliência do JWK Provider.
 * Permite que cada serviço ajuste o cache e rate limit conforme seu tráfego.
 */
data class JwkProviderConfig(
    val cacheSize: Long = 10,
    val cacheExpiresIn: Long = 24,
    val cacheUnit: TimeUnit = TimeUnit.HOURS,
    val rateLimitBucket: Long = 10,
    val rateLimitRefillRate: Long = 1,
    val rateLimitUnit: TimeUnit = TimeUnit.MINUTES,
    val customJwksUrl: String? = null
) {
    companion object {
        /**
         * Para serviços administrativos, internos ou cron jobs.
         * Pouca concorrência, baixo risco de "cold start" com muitas requisições simultâneas.
         */
        val LOW_TRAFFIC = JwkProviderConfig(
            cacheSize = 5,
            rateLimitBucket = 10,
            rateLimitRefillRate = 1
        )

        /**
         * Padrão recomendado para lançamento.
         * Equilibra proteção contra loop infinito e disponibilidade para usuários pagantes.
         * O bucket de 25 garante que um container Cloud Run novo aguente um pico inicial moderado.
         */
        val MEDIUM_TRAFFIC = JwkProviderConfig(
            cacheSize = 10,
            rateLimitBucket = 25,
            rateLimitRefillRate = 5,
            cacheExpiresIn = 24
        )

        /**
         * Para serviços críticos, gateways ou features virais.
         * Prioriza a disponibilidade total.
         * O bucket de 100 é essencial para Cloud Run sob auto-scaling agressivo,
         * garantindo que múltiplas threads possam buscar chaves sem serem bloqueadas pelo rate limit local.
         */
        val HIGH_TRAFFIC = JwkProviderConfig(
            cacheSize = 10,
            rateLimitBucket = 100,
            rateLimitRefillRate = 10,
            cacheExpiresIn = 24
        )
    }
}

fun String.toUrl(): URL = URI.create(this).toURL()