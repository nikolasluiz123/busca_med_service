package br.com.buscamed.data.client.anvisa

import br.com.buscamed.data.client.anvisa.dto.AnvisaDatasetResponseDTO
import br.com.buscamed.data.client.anvisa.exception.AnvisaIntegrationException
import br.com.buscamed.domain.service.AnvisaIntegrationService
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.slf4j.LoggerFactory

class AnvisaIntegrationKtorClient(
    private val httpClient: HttpClient
) : AnvisaIntegrationService {

    private val logger = LoggerFactory.getLogger(this::class.java)
    private val datasetUrl = "https://dados.gov.br/api/publico/conjuntos-dados/preco-de-medicamentos-no-brasil-consumidor"

    override suspend fun downloadPricesCsv(): ByteArray {
        val datasetResponse = httpClient.get(datasetUrl)

        if (!datasetResponse.status.isSuccess()) {
            throw AnvisaIntegrationException(
                technicalMessage = "Falha na integração com a ANVISA ao buscar metadados. Status HTTP: ${datasetResponse.status}",
                statusCode = datasetResponse.status.value
            )
        }

        val metadata: AnvisaDatasetResponseDTO = datasetResponse.body()

        val csvResource = metadata.resources.firstOrNull { it.format.equals("CSV", ignoreCase = true) }
        val csvResourceUrl = csvResource?.url ?: throw AnvisaIntegrationException(
            technicalMessage = "O recurso no formato CSV não foi encontrado no payload da ANVISA.",
            statusCode = HttpStatusCode.NotFound.value
        )

        var lastLoggedProgress = 0L

        return httpClient.prepareGet(csvResourceUrl) {
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
}