package br.com.buscamed.data.parser

import br.com.buscamed.domain.model.anvisa.enumeration.AnvisaProductType
import br.com.buscamed.domain.model.anvisa.enumeration.MedicationStripe
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.FileNotFoundException
import java.io.InputStreamReader

/**
 * Unit tests for [ApacheCommonsAnvisaCsvParser].
 */
class ApacheCommonsAnvisaCsvParserTest {

    private val parser = ApacheCommonsAnvisaCsvParser()

    private fun getCsvBytesFromResources(fileName: String): ByteArray {
        val inputStream = this::class.java.getResourceAsStream("/csv/$fileName")
            ?: throw FileNotFoundException("Arquivo de teste /csv/$fileName não encontrado nos resources.")
        return inputStream.readBytes()
    }

    @Test
    fun parse_onlyHeadersCsv_returnsEmptyList() {
        val dummyLines = List(41) { "Header line $it;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;" }
        val csvBytes = dummyLines.joinToString("\n").toByteArray()

        val result = parser.parse(csvBytes)

        assertTrue(result.medications.isEmpty())
        assertTrue(result.cleanedCsvBytes.isNotEmpty())
    }

    @Test
    fun parse_validCsvFile_extractsAndMapsDataCorrectly() {
        val csvBytes = getCsvBytesFromResources("valid_anvisa.csv")

        val result = parser.parse(csvBytes)
        val medications = result.medications

        assertEquals(5, medications.size)

        val abatacepte = medications.first { it.ggremCode == "505107701157215" }
        assertEquals(1, abatacepte.activeIngredients.size)
        assertEquals("ABATACEPTE", abatacepte.activeIngredients.first())
        assertEquals(AnvisaProductType.BIOLOGIC, abatacepte.productType)
        assertEquals(MedicationStripe.RED, abatacepte.stripe)
        assertTrue(abatacepte.isHospitalRestriction)

        val epocler = medications.first { it.ggremCode == "533021120076717" }
        assertEquals(3, epocler.activeIngredients.size)
        assertTrue(epocler.activeIngredients.contains("ACETILRACEMETIONINA"))
        assertTrue(epocler.activeIngredients.contains("BETAÍNA"))
        assertEquals(AnvisaProductType.SPECIFIC, epocler.productType)
        assertEquals(MedicationStripe.UNSTRIPED, epocler.stripe)
        assertFalse(epocler.isHospitalRestriction)

        val acebrofilina = medications.first { it.ggremCode == "507728901136116" }
        assertEquals(AnvisaProductType.GENERIC, acebrofilina.productType)
        assertFalse(acebrofilina.isHospitalRestriction)

        val baycuten = medications.first { it.ggremCode == "538912020009303" }
        assertEquals(2, baycuten.activeIngredients.size)
        assertTrue(baycuten.activeIngredients.contains("21-ACETATO DE DEXAMETASONA"))
        assertEquals(AnvisaProductType.NEW, baycuten.productType)
        assertEquals(MedicationStripe.UNSTRIPED, baycuten.stripe)
        assertFalse(baycuten.isHospitalRestriction)

        val lisomuc = medications.first { it.ggremCode == "517113100013304" }
        assertEquals(AnvisaProductType.SIMILAR, lisomuc.productType)
        assertEquals(MedicationStripe.BLACK, lisomuc.stripe)
        assertFalse(lisomuc.isHospitalRestriction)
    }

    @Test
    fun parse_validCsvFile_generatesCleanedCsvBytesOnlyWithRequiredColumns() {
        val csvBytes = getCsvBytesFromResources("valid_anvisa.csv")

        val result = parser.parse(csvBytes)

        val reader = InputStreamReader(ByteArrayInputStream(result.cleanedCsvBytes), Charsets.UTF_8)
        val csvFormat = CSVFormat.DEFAULT.builder().setDelimiter(';').setHeader().get()
        val parsedCleaned = CSVParser.builder().setReader(reader).setFormat(csvFormat).get()

        val records = parsedCleaned.records

        assertEquals(5, records.size)

        val firstRecord = records[0]
        assertEquals("505107701157215", firstRecord.get("CODIGO_GGREM"))
        assertEquals("ABATACEPTE", firstRecord.get("SUBSTANCIA").replace(";", "; "))
        assertEquals("56.998.982/0001-07", firstRecord.get("CNPJ"))
        assertEquals("Biológico", firstRecord.get("TIPO_PRODUTO"))
        assertEquals("Sim", firstRecord.get("RESTRICAO_HOSPITALAR"))
        assertEquals("Tarja Vermelha", firstRecord.get("TARJA"))
    }

    @Test
    fun parse_fileWithMalformedLines_skipsErrorsAndParsesValidLines() {
        val csvBytes = getCsvBytesFromResources("invalid_lines_anvisa.csv")

        val result = parser.parse(csvBytes)
        val medications = result.medications

        assertEquals(2, medications.size)
        assertEquals("524825010013107", medications[0].ggremCode)
        assertEquals("508520080013207", medications[1].ggremCode)
    }
}