package br.com.buscamed.data.client.anvisa

import br.com.buscamed.data.client.anvisa.dto.AnvisaDatasetResponseDTO
import br.com.buscamed.data.client.anvisa.dto.AnvisaLeafletQueryResponseDTO
import br.com.buscamed.data.client.anvisa.exception.AnvisaIntegrationException
import br.com.buscamed.domain.model.anvisa.AnvisaLeafletIds
import br.com.buscamed.domain.service.AnvisaIntegrationService
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.slf4j.LoggerFactory

/**
 * Cliente Ktor para integração com a API de dados abertos da ANVISA.
 *
 * @property httpClient O cliente HTTP para realizar as requisições.
 */
class AnvisaIntegrationKtorClient(
    private val httpClient: HttpClient
) : AnvisaIntegrationService {

    private val logger = LoggerFactory.getLogger(this::class.java)
    private val datasetMedicationsUrl = "https://dados.gov.br/api/publico/conjuntos-dados/preco-de-medicamentos-no-brasil-consumidor"
    private val medicationsDirectCsvLink = "https://dados.anvisa.gov.br/dados/TA_PRECO_MEDICAMENTO.csv"
    private val queryLeafletUrl = "https://consultas.anvisa.gov.br/api/consulta/bulario"
    private val downloadLeafletUrl = "https://consultas.anvisa.gov.br/api/consulta/medicamentos/arquivo/bula/parecer/"

    /**
     * Realiza o download do arquivo CSV de preços de medicamentos disponibilizado pela ANVISA.
     *
     * Primeiro, tenta buscar os metadados do conjunto de dados para encontrar a URL do recurso CSV.
     * Se essa chamada der sucesso, faz o download do arquivo a partir da URL encontrada, registrando o progresso.
     *
     * Se não for possível chamar [datasetUrl] por algum motivo, tenta fazer uso do link direto de download do CSV.
     *
     * @return Um [ByteArray] contendo os dados do arquivo CSV.
     * @throws AnvisaIntegrationException Se ocorrer uma falha na comunicação com a API da ANVISA
     * ou se o recurso CSV não for encontrado.
     */
    override suspend fun downloadPricesCsv(): ByteArray {
        val datasetResponse = httpClient.get(datasetMedicationsUrl)

        val targetUrl = if (datasetResponse.status.isSuccess()) {
            val metadata: AnvisaDatasetResponseDTO = datasetResponse.body()
            val csvResource = metadata.resources.firstOrNull { it.format.equals("CSV", ignoreCase = true) }

            csvResource?.url ?: throw AnvisaIntegrationException(
                technicalMessage = "O recurso no formato CSV não foi encontrado no payload da ANVISA.",
                statusCode = HttpStatusCode.NotFound.value
            )
        } else {
            medicationsDirectCsvLink
        }

        var lastLoggedProgress = 0L

        return httpClient.prepareGet(targetUrl) {
            onDownload { bytesSentTotal, contentLength ->
                if (contentLength != null && contentLength > 0) {
                    val progress = (bytesSentTotal * 100) / contentLength
                    if (progress - lastLoggedProgress >= 10 || progress == 100L) {
                        logger.info("Progresso do download: $progress% ($bytesSentTotal / $contentLength bytes)")
                        lastLoggedProgress = progress
                    }
                } else {
                    val mbDownloaded = bytesSentTotal / (1024 * 1024)
                    if (mbDownloaded > lastLoggedProgress) {
                        logger.info("Progresso do download: $mbDownloaded MB baixados...")
                        lastLoggedProgress = mbDownloaded
                    }
                }
            }
        }.execute {
            if (!it.status.isSuccess()) {
                throw AnvisaIntegrationException(
                    technicalMessage = "Falha ao realizar o download do arquivo CSV. Status HTTP: ${it.status}",
                    statusCode = it.status.value
                )
            }

            it.body<ByteArray>()
        }
    }

    override suspend fun fetchLeafletIds(registerNumber: String): AnvisaLeafletIds? {
        val response = httpClient.get(queryLeafletUrl) {
            accept(ContentType.Application.Json)
            header("Authorization", "Guest")

            parameter("count", "1")
            parameter("page", "1")
            parameter("filter[numeroRegistro]", registerNumber)
        }

        if (response.status.isSuccess()) {
            val data: AnvisaLeafletQueryResponseDTO = response.body()
            val first = data.content.firstOrNull() ?: return null

            return AnvisaLeafletIds(
                patientLeafletId = first.idBulaPacienteProtegido,
                professionalLeafletId = first.idBulaProfissionalProtegido
            )
        }
        return null
    }

    override suspend fun downloadLeafletPdf(fileId: String): ByteArray {
        val targetUrl = "$downloadLeafletUrl$fileId/"

        return httpClient.prepareGet(targetUrl) {
            onDownload { bytesSentTotal, contentLength ->
                if (contentLength != null && contentLength > 0) {
                    val progress = (bytesSentTotal * 100) / contentLength
                    logger.debug("Download Bula $fileId: $progress% ($bytesSentTotal bytes)")
                }
            }
        }.execute { response ->
            if (!response.status.isSuccess()) {
                throw AnvisaIntegrationException(
                    technicalMessage = "Erro ao baixar PDF da bula $fileId. Status: ${response.status}",
                    statusCode = response.status.value
                )
            }
            response.body()
        }
    }
}
