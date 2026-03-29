package br.com.buscamed.core.config

import br.com.buscamed.api.v1.anvisa.AnvisaController
import br.com.buscamed.api.v1.pillpack.PillPackController
import br.com.buscamed.api.v1.prescription.PrescriptionController
import br.com.buscamed.core.config.properties.GeminiConfig
import br.com.buscamed.data.client.anvisa.AnvisaIntegrationClient
import br.com.buscamed.data.client.anvisa.AnvisaIntegrationKtorClient
import br.com.buscamed.data.client.core.HttpClientFactory
import br.com.buscamed.data.client.gemini.image.GeminiMedicalPrescriptionImageProcessClient
import br.com.buscamed.data.client.gemini.image.GeminiPillPackImageProcessClient
import br.com.buscamed.data.client.gemini.text.GeminiMedicalPrescriptionTextProcessClient
import br.com.buscamed.data.client.gemini.text.GeminiPillPackTextProcessClient
import br.com.buscamed.data.client.storage.google.csv.AnvisaCsvGoogleStorageClient
import br.com.buscamed.data.client.storage.google.image.MedicalPrescriptionGoogleStorageClient
import br.com.buscamed.data.client.storage.google.image.PillPackGoogleStorageClient
import br.com.buscamed.data.datasource.FirestoreAnvisaMedicationDataSource
import br.com.buscamed.data.datasource.FirestoreMedicalPrescriptionExecutionHistoryDataSource
import br.com.buscamed.data.datasource.FirestorePillPackExecutionHistoryDataSource
import br.com.buscamed.data.datasource.FirestoreSystemProcessControlDataSource
import br.com.buscamed.data.datasource.interfaces.AnvisaMedicationDataSource
import br.com.buscamed.data.datasource.interfaces.LLMExecutionHistoryDataSource
import br.com.buscamed.data.datasource.interfaces.SystemProcessControlDataSource
import br.com.buscamed.data.parser.ApacheCommonsAnvisaCsvParser
import br.com.buscamed.data.repository.AnvisaMedicationRepositoryImpl
import br.com.buscamed.data.repository.MedicalPrescriptionExecutionHistoryRepositoryImpl
import br.com.buscamed.data.repository.PillPackExecutionHistoryRepositoryImpl
import br.com.buscamed.data.repository.SystemProcessControlRepositoryImpl
import br.com.buscamed.domain.parser.AnvisaCsvParser
import br.com.buscamed.domain.repository.AnvisaMedicationRepository
import br.com.buscamed.domain.repository.LLMExecutionHistoryRepository
import br.com.buscamed.domain.repository.SystemProcessControlRepository
import br.com.buscamed.domain.usecase.*
import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.FirestoreOptions
import com.google.cloud.storage.Storage
import com.google.cloud.storage.StorageOptions
import io.ktor.client.*
import io.ktor.server.application.*
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

/**
 * Agrupa as chaves de identificação (Qualifiers) utilizadas no Koin
 * para resolver ambiguidades de injeção de dependências.
 */
object DiQualifiers {
    const val DS_MEDICAL_PRESCRIPTION = "DataSourceMedicalPrescription"
    const val DS_PILL_PACK = "DataSourcePillPack"

    const val REPO_MEDICAL_PRESCRIPTION = "RepositoryMedicalPrescription"
    const val REPO_PILL_PACK = "RepositoryPillPack"

    const val UC_DOWNLOAD_MEDICAL_PRESCRIPTION_IMAGE = "DownloadMedicalPrescriptionImageUseCase"
    const val UC_DOWNLOAD_PILL_PACK_IMAGE = "DownloadPillPackImageUseCase"

    const val HTTP_CLIENT_ANVISA = "HttpClientAnvisa"
}

/**
 * Configura o plugin do Koin para a injeção de dependências da aplicação.
 *
 * @receiver Application O contexto da aplicação Ktor.
 */
fun Application.configureDI() {
    install(Koin) {
        slf4jLogger()
        modules(appModule(this@configureDI.environment))
    }
}

/**
 * Define as regras de provisão de instâncias para os componentes do sistema.
 *
 * @param environment O ambiente da aplicação, utilizado para resgatar configurações.
 * @return O módulo contendo as definições do Koin.
 */
fun appModule(environment: ApplicationEnvironment) = module {

    single {
        GeminiConfig(
            projectId = environment.config.property("buscamed.gcp.project_id").getString(),
            location = environment.config.property("buscamed.gcp.region").getString()
        )
    }

    single<Firestore> {
        val firestoreDbId = environment.config.propertyOrNull("buscamed.gcp.firestore.database_id")?.getString() ?: "dev-db"

        FirestoreOptions.newBuilder()
            .setDatabaseId(firestoreDbId)
            .build()
            .service
    }

    single<Storage> {
        StorageOptions.getDefaultInstance().service
    }

    single<HttpClient> {
        HttpClientFactory.createClient()
    }

    single<HttpClient>(named(DiQualifiers.HTTP_CLIENT_ANVISA)) {
        HttpClientFactory.createClient(
            connectTimeoutMillis = 300_000,
            requestTimeoutMillis = 600_000
        )
    }

    factory<LLMExecutionHistoryDataSource>(named(DiQualifiers.DS_MEDICAL_PRESCRIPTION)) {
        FirestoreMedicalPrescriptionExecutionHistoryDataSource(db = get())
    }

    factory<LLMExecutionHistoryDataSource>(named(DiQualifiers.DS_PILL_PACK)) {
        FirestorePillPackExecutionHistoryDataSource(db = get())
    }

    factory<AnvisaMedicationDataSource> {
        FirestoreAnvisaMedicationDataSource(db = get())
    }

    factory<SystemProcessControlDataSource> {
        FirestoreSystemProcessControlDataSource(db = get())
    }

    factory<LLMExecutionHistoryRepository>(named(DiQualifiers.REPO_MEDICAL_PRESCRIPTION)) {
        MedicalPrescriptionExecutionHistoryRepositoryImpl(
            dataSource = get(named(DiQualifiers.DS_MEDICAL_PRESCRIPTION))
        )
    }

    factory<LLMExecutionHistoryRepository>(named(DiQualifiers.REPO_PILL_PACK)) {
        PillPackExecutionHistoryRepositoryImpl(
            dataSource = get(named(DiQualifiers.DS_PILL_PACK))
        )
    }

    factory<AnvisaMedicationRepository> {
        AnvisaMedicationRepositoryImpl(dataSource = get())
    }

    factory<SystemProcessControlRepository> {
        SystemProcessControlRepositoryImpl(dataSource = get())
    }

    single { MedicalPrescriptionGoogleStorageClient(storage = get()) }
    single { PillPackGoogleStorageClient(storage = get()) }
    single { AnvisaCsvGoogleStorageClient(storage = get()) }

    single { GeminiMedicalPrescriptionImageProcessClient(config = get()) }
    single { GeminiPillPackImageProcessClient(config = get()) }
    single { GeminiMedicalPrescriptionTextProcessClient(config = get()) }
    single { GeminiPillPackTextProcessClient(config = get()) }

    single<AnvisaIntegrationClient> {
        AnvisaIntegrationKtorClient(httpClient = get(named(DiQualifiers.HTTP_CLIENT_ANVISA)))
    }

    single<AnvisaCsvParser> { ApacheCommonsAnvisaCsvParser() }

    factory {
        ProcessMedicalPrescriptionImageUseCase(
            executionHistoryRepository = get(named(DiQualifiers.REPO_MEDICAL_PRESCRIPTION)),
            geminiClient = get(),
            storageClient = get()
        )
    }

    factory {
        ProcessPillPackImageUseCase(
            executionHistoryRepository = get(named(DiQualifiers.REPO_PILL_PACK)),
            geminiClient = get(),
            storageClient = get()
        )
    }

    factory {
        ProcessMedicalPrescriptionTextUseCase(
            executionHistoryRepository = get(named(DiQualifiers.REPO_MEDICAL_PRESCRIPTION)),
            geminiClient = get()
        )
    }

    factory {
        ProcessPillPackTextUseCase(
            executionHistoryRepository = get(named(DiQualifiers.REPO_PILL_PACK)),
            geminiClient = get()
        )
    }

    factory {
        GetMedicalPrescriptionHistoryUseCase(
            repository = get(named(DiQualifiers.REPO_MEDICAL_PRESCRIPTION))
        )
    }

    factory {
        GetPillPackHistoryUseCase(
            repository = get(named(DiQualifiers.REPO_PILL_PACK))
        )
    }

    factory { GetContentTypeByExtensionUseCase() }

    factory(named(DiQualifiers.UC_DOWNLOAD_MEDICAL_PRESCRIPTION_IMAGE)) {
        DownloadImageUseCase(
            executionHistoryDataSource = get(named(DiQualifiers.DS_MEDICAL_PRESCRIPTION)),
            storageClient = get<MedicalPrescriptionGoogleStorageClient>(),
            getContentTypeByExtensionUseCase = get()
        )
    }

    factory(named(DiQualifiers.UC_DOWNLOAD_PILL_PACK_IMAGE)) {
        DownloadImageUseCase(
            executionHistoryDataSource = get(named(DiQualifiers.DS_PILL_PACK)),
            storageClient = get<PillPackGoogleStorageClient>(),
            getContentTypeByExtensionUseCase = get()
        )
    }

    factory {
        PrescriptionController(
            processImageUseCase = get(),
            processTextUseCase = get(),
            getHistoryUseCase = get(),
            downloadImageUseCase = get(named(DiQualifiers.UC_DOWNLOAD_MEDICAL_PRESCRIPTION_IMAGE))
        )
    }

    factory {
        PillPackController(
            processImageUseCase = get(),
            processTextUseCase = get(),
            getHistoryUseCase = get(),
            downloadImageUseCase = get(named(DiQualifiers.UC_DOWNLOAD_PILL_PACK_IMAGE))
        )
    }

    factory {
        ImportAnvisaInformationUseCase(
            integrationClient = get(),
            csvParser = get(),
            medicationRepository = get(),
            storageClient = get(),
            processControlRepository = get()
        )
    }

    factory {
        AnvisaController(
            importAnvisaInformationUseCase = get()
        )
    }
}