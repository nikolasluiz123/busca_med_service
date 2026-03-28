package br.com.buscamed.domain.parser

/**
 * Contrato para o serviço de extração e formatação de dados de medicamentos a partir de arquivos CSV.
 */
interface AnvisaCsvParser {

    /**
     * Realiza o parsing de um arquivo CSV em formato binário, extraindo os dados para uma lista de entidades
     * de domínio e gerando uma nova versão limpa do arquivo.
     *
     * @param csvBytes O conteúdo do arquivo CSV original em array de bytes.
     * @return Um objeto [AnvisaCsvParseResult] contendo a lista processada e o novo arquivo gerado em memória.
     */
    fun parse(csvBytes: ByteArray): AnvisaCsvParseResult
}