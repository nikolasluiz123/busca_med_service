package br.com.buscamed.data.parser

import br.com.buscamed.domain.model.anvisa.AnvisaMedication
import br.com.buscamed.domain.model.anvisa.enumeration.AnvisaProductType
import br.com.buscamed.domain.model.anvisa.enumeration.MedicationStripe
import br.com.buscamed.domain.parser.AnvisaCsvParseResult
import br.com.buscamed.domain.parser.AnvisaCsvParser
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVPrinter
import org.apache.commons.csv.CSVRecord
import org.slf4j.LoggerFactory
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter

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
        val headerMap = mutableMapOf<String, Int>()

        for (record in parser) {
            currentLine++

            if (record.size() == 0) continue

            if (headerMap.isEmpty()) {
                val firstCol = record.get(0).trim()
                if (firstCol == "SUBSTÂNCIA") {
                    for (i in 0 until record.size()) {
                        headerMap[record.get(i).trim()] = i
                    }
                }
                continue
            }

            try {
                val ggremCode = getColumnValue(record, headerMap, "CÓDIGO GGREM")

                if (ggremCode.isEmpty()) continue

                val rawActiveIngredients = getColumnValue(record, headerMap, "SUBSTÂNCIA")
                val cnpj = getColumnValue(record, headerMap, "CNPJ")
                val laboratory = getColumnValue(record, headerMap, "LABORATÓRIO")
                val ean1 = parseEan(getColumnValue(record, headerMap, "EAN 1"))
                val ean2 = parseEan(getColumnValue(record, headerMap, "EAN 2"))
                val ean3 = parseEan(getColumnValue(record, headerMap, "EAN 3"))
                val productName = getColumnValue(record, headerMap, "PRODUTO")
                val presentation = getColumnValue(record, headerMap, "APRESENTAÇÃO")
                val therapeuticClass = getColumnValue(record, headerMap, "CLASSE TERAPÊUTICA")
                val productTypeStr = getColumnValue(record, headerMap, "TIPO DE PRODUTO (STATUS DO PRODUTO)")
                val hospitalRestrictionStr = getColumnValue(record, headerMap, "RESTRIÇÃO HOSPITALAR")
                val stripeStr = getColumnValue(record, headerMap, "TARJA")

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
     * Busca o valor de uma coluna com base no nome exato registrado no mapeamento de cabeçalho.
     *
     * @param record O registro CSV atual
     * @param headerMap O mapa contendo os nomes das colunas e seus respectivos índices
     * @param columnName O nome exato da coluna a ser buscada
     * @return O valor em formato de string ou string vazia caso a coluna não exista no registro
     */
    private fun getColumnValue(record: CSVRecord, headerMap: Map<String, Int>, columnName: String): String {
        val idx = headerMap[columnName]

        return if (idx != null && idx < record.size()) {
            record.get(idx).trim()
        } else {
            ""
        }
    }

    /**
     * Analisa o valor bruto do EAN e retorna nulo se for vazio ou um traço.
     *
     * @param value O valor original contido no CSV
     * @return O código EAN ou null
     */
    private fun parseEan(value: String): String? {
        val cleanValue = value.trim()
        return if (cleanValue == "-" || cleanValue.isEmpty()) null else cleanValue
    }
}