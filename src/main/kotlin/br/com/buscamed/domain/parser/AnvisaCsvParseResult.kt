package br.com.buscamed.domain.parser

import br.com.buscamed.domain.model.anvisa.AnvisaMedication

/**
 * Resultado do processamento de um arquivo CSV da ANVISA.
 *
 * @property medications Lista de medicamentos extraídos com sucesso.
 * @property cleanedCsvBytes O conteúdo binário do novo arquivo CSV contendo apenas os dados válidos e mapeados.
 */
class AnvisaCsvParseResult(
    val medications: List<AnvisaMedication>,
    val cleanedCsvBytes: ByteArray
)