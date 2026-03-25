package br.com.buscamed.data.client.core.llm

import java.io.FileNotFoundException

abstract class LLMProcessClient {
    protected abstract val modelId: String
    protected abstract val promptVersion: String
    protected abstract val promptsDirectoryName: String
    protected abstract val promptFileName: String

    protected fun getSystemInstruction(specificPromptVersion: String? = null): String {
        val version = specificPromptVersion ?: promptVersion
        val fileName = "prompts/$promptsDirectoryName/${promptFileName}_$version.txt"
        val resource = this::class.java.classLoader.getResource(fileName)
            ?: throw FileNotFoundException("Prompt file not found in this path: $fileName")

        return resource.readText()
    }
}