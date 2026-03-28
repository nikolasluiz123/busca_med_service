package br.com.buscamed.data.parser

import br.com.buscamed.domain.model.anvisa.AnvisaMedication
import br.com.buscamed.domain.model.anvisa.enumeration.AnvisaProductType
import br.com.buscamed.domain.model.anvisa.enumeration.MedicationStripe
import br.com.buscamed.domain.parser.AnvisaCsvParseResult
import br.com.buscamed.domain.parser.AnvisaCsvParser
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVPrinter
import org.slf4j.LoggerFactory
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.nio.charset.Charset

/**
 * Implementação de [AnvisaCsvParser] utilizando a biblioteca Apache Commons CSV.
 */
class ApacheCommonsAnvisaCsvParser : AnvisaCsvParser {
    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun parse(csvBytes: ByteArray): AnvisaCsvParseResult {
        val resultList = mutableListOf<AnvisaMedication>()
        val charset = Charsets.UTF_8

        val reader = InputStreamReader(ByteArrayInputStream(csvBytes), charset)
        val csvFormat = CSVFormat.DEFAULT.builder().setDelimiter(';').get()
        val parser = CSVParser.builder().setReader(reader).setFormat(csvFormat).get()

        val outputStream = ByteArrayOutputStream()
        val writer = OutputStreamWriter(outputStream, charset)
        val printer = CSVPrinter(writer, csvFormat)

        printer.printRecord(
            "CODIGO_GGREM", "SUBSTANCIA", "CNPJ", "LABORATORIO", "EAN_1", "EAN_2", "EAN_3",
            "PRODUTO", "APRESENTACAO", "CLASSE_TERAPEUTICA", "TIPO_PRODUTO",
            "RESTRICAO_HOSPITALAR", "TARJA"
        )

        var currentLine = 0

        for (record in parser) {
            currentLine++

            if (currentLine <= 41) {
                continue
            }

            try {
                val ggremCode = record.get(3).trim()
                val rawActiveIngredients = record.get(0)
                val cnpj = record.get(1)
                val laboratory = record.get(2)
                val ean1 = parseEan(record.get(5))
                val ean2 = parseEan(record.get(6))
                val ean3 = parseEan(record.get(7))
                val productName = record.get(8)
                val presentation = record.get(9)
                val therapeuticClass = record.get(10)
                val productTypeStr = record.get(11)
                val hospitalRestrictionStr = record.get(65)
                val stripeStr = record.get(72)

                val activeIngredientsList = rawActiveIngredients.split(";")
                    .map { it.trim() }
                    .filter { it.isNotEmpty() }

                val isHospitalRestriction = hospitalRestrictionStr.trim().equals("Sim", ignoreCase = true)
                val productType = AnvisaProductType.fromDescription(productTypeStr)
                val stripe = MedicationStripe.fromDescription(stripeStr)

                val medication = AnvisaMedication(
                    ggremCode = ggremCode,
                    activeIngredients = activeIngredientsList,
                    cnpj = cnpj,
                    laboratory = laboratory,
                    ean1 = ean1,
                    ean2 = ean2,
                    ean3 = ean3,
                    productName = productName,
                    presentation = presentation,
                    therapeuticClass = therapeuticClass,
                    productType = productType,
                    isHospitalRestriction = isHospitalRestriction,
                    stripe = stripe
                )

                resultList.add(medication)

                printer.printRecord(
                    ggremCode,
                    rawActiveIngredients,
                    cnpj,
                    laboratory,
                    ean1 ?: "",
                    ean2 ?: "",
                    ean3 ?: "",
                    productName,
                    presentation,
                    therapeuticClass,
                    productType.description,
                    if (isHospitalRestriction) "Sim" else "Não",
                    stripe.description
                )
            } catch (e: Exception) {
                logger.warn("Não foi possível realizar o parse da linha $currentLine", e)
                continue
            }
        }

        parser.close()
        printer.flush()
        printer.close()

        return AnvisaCsvParseResult(
            medications = resultList,
            cleanedCsvBytes = outputStream.toByteArray()
        )
    }

    /**
     * Trata o valor extraído da coluna de código de barras.
     *
     * @param value O texto presente na coluna do CSV.
     * @return A string limpa ou null se o valor indicar ausência de dado.
     */
    private fun parseEan(value: String): String? {
        val cleanValue = value.trim()
        return if (cleanValue == "-" || cleanValue.isEmpty()) null else cleanValue
    }
}