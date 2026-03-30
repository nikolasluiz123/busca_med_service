package br.com.buscamed.domain.exceptions

/**
 * Classe base para todas as exceções customizadas da aplicação BuscaMed.
 *
 * Garante que toda exceção tenha uma mensagem para o usuário, um código de status HTTP
 * e um código de erro interno para facilitar a identificação e o tratamento.
 *
 * @property userMessage A mensagem de erro que pode ser exibida ao usuário final.
 * @property statusCode O código de status HTTP associado à exceção.
 * @property errorCode Um código de erro único que identifica a natureza da falha.
 * @param cause A exceção original que causou esta exceção.
 */
abstract class BuscaMedException(
    val userMessage: String,
    val statusCode: Int,
    val errorCode: String,
    cause: Throwable? = null
) : RuntimeException(userMessage, cause)
