package br.com.buscamed.data.client.core.llm

import java.io.FileNotFoundException

/**
 * Cliente base para o processamento de requisições a Large Language Models (LLMs).
 *
 * Define o fluxo comum de recuperação de instruções do sistema (prompts)
 * do diretório de resources.
 */
abstract class LLMProcessClient {
    /** O identificador do modelo da LLM a ser utilizado. */
    protected abstract val modelId: String
    /** A versão atual do prompt, útil para versionamento de comportamento. */
    protected abstract val promptVersion: String
    /** O nome do diretório contendo os prompts da integração. */
    protected abstract val promptsDirectoryName: String
    /** O nome base do arquivo contendo o texto do prompt. */
    protected abstract val promptFileName: String

    /**
     * Lê e retorna a instrução de sistema a partir do arquivo de texto localizado em `prompts/`.
     *
     * O arquivo procurado tem o formato `[promptFileName]_[promptVersion].txt`.
     *
     * @return O conteúdo completo da instrução de sistema carregada.
     * @throws FileNotFoundException Se o arquivo não existir no classloader.
     */
    protected fun getSystemInstruction(): String {
        val fileName = "prompts/$promptsDirectoryName/${getFullPromptFileName()}.txt"
        val resource = this::class.java.classLoader.getResource(fileName)
            ?: throw FileNotFoundException("Prompt file not found in this path: $fileName")

        return resource.readText()
    }

    /**
     * Retorna o nome do arquivo combinado com a versão utilizada.
     */
    protected fun getFullPromptFileName(): String = "${promptFileName}_$promptVersion"
}
