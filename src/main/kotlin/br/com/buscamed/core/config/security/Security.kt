package br.com.buscamed.core.config.security

import com.auth0.jwk.JwkProviderBuilder
import io.ktor.server.application.*
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.jwt

/**
 * Configura os provedores de autenticação (Security) da aplicação Ktor.
 *
 * Lê as variáveis de ambiente necessárias e inicializa as estratégias de autenticação
 * baseadas em JWT, suportando tanto usuários via Firebase Auth quanto
 * comunicação service-to-service via Google OIDC.
 */
fun Application.configureSecurity() {
    val projectId = environment.config.property("buscamed.gcp.project_id").getString()
    val serviceAudience = environment.config.propertyOrNull("buscamed.gcp.service_audience")?.getString() ?: projectId

    val firebaseStrategy = AuthStrategy.FirebaseAuth(
        projectId = projectId,
        jwkConfig = JwkProviderConfig.MEDIUM_TRAFFIC
    )

    val googleOidcStrategy = AuthStrategy.GoogleOidc(
        targetAudience = serviceAudience,
        jwkConfig = JwkProviderConfig.LOW_TRAFFIC
    )

    configureSecurityStrategies(firebaseStrategy, googleOidcStrategy)
}

/**
 * Instala o plugin de Autenticação do Ktor e registra as estratégias fornecidas.
 *
 * Cada estratégia configura o seu próprio provedor de chaves (JWK Provider) com
 * regras de cache e rate limit específicas, além de definir os validadores de
 * audience e issuer apropriados.
 *
 * @param strategies Uma ou mais implementações de [AuthStrategy] a serem registradas.
 */
private fun Application.configureSecurityStrategies(vararg strategies: AuthStrategy) {
    install(Authentication) {
        strategies.forEach { strategy ->
            val jwkSettings = strategy.jwkConfig
            val urlToUse = (jwkSettings.customJwksUrl ?: strategy.defaultJwksUrl).toUrl()

            val jwkProvider = JwkProviderBuilder(urlToUse)
                .cached(jwkSettings.cacheSize, jwkSettings.cacheExpiresIn, jwkSettings.cacheUnit)
                .rateLimited(jwkSettings.rateLimitBucket, jwkSettings.rateLimitRefillRate, jwkSettings.rateLimitUnit)
                .build()

            jwt(strategy.authName) {
                realm = AuthConstants.REALM

                val expectedIssuer = strategy.getIssuer()

                verifier(jwkProvider, expectedIssuer) {
                    withAudience(strategy.getAudience())
                    withIssuer(expectedIssuer)
                }

                validate { credential ->
                    strategy.validate(credential)
                }
            }
        }
    }
}
