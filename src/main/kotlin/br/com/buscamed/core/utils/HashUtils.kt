package br.com.buscamed.core.utils

import java.security.MessageDigest

/**
 * Utilitário para operações de criptografia e hash.
 */
object HashUtils {

    /**
     * Gera uma string de hash SHA-256 a partir de um array de bytes.
     *
     * @param bytes O conteúdo binário a ser submetido ao hash.
     * @return O hash em formato hexadecimal.
     */
    fun generateSha256(bytes: ByteArray): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(bytes)
        return hashBytes.joinToString("") { "%02x".format(it) }
    }
}