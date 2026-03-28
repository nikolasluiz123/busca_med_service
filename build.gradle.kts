plugins {
    kotlin("jvm") version "2.3.0"
    kotlin("plugin.serialization") version "2.3.0"

    id("io.ktor.plugin") version "3.4.1"
}

group = "br.com"
version = "0.1.0"

application {
    mainClass = "io.ktor.server.netty.EngineMain"
}

repositories {
    mavenCentral()
    mavenLocal()
}

kotlin {
    jvmToolchain(23)
}

dependencies {
    implementation(platform("io.ktor:ktor-bom:${project.property("ktor_version")}"))
    implementation("io.ktor:ktor-server-core")
    implementation("io.ktor:ktor-server-netty")
    implementation("io.ktor:ktor-server-status-pages")
    implementation("io.ktor:ktor-server-cors")
    implementation("io.ktor:ktor-serialization-kotlinx-json")
    implementation("io.ktor:ktor-server-content-negotiation")
    implementation("io.ktor:ktor-server-auth")
    implementation("io.ktor:ktor-server-auth-jwt")
    implementation("io.ktor:ktor-server-call-logging")
    implementation("io.ktor:ktor-server-config-yaml")
    implementation("io.ktor:ktor-client-content-negotiation")
    implementation("io.ktor:ktor-client-cio")
    implementation("io.ktor:ktor-server-openapi")
    implementation("io.ktor:ktor-server-swagger")

    implementation(platform("com.google.cloud:libraries-bom:${project.property("google_cloud_bom_version")}"))
    implementation("com.google.cloud:google-cloud-firestore")
    implementation("com.google.cloud:google-cloud-storage")
    implementation("com.google.genai:google-genai:1.32.0")

    implementation("com.auth0:jwks-rsa:0.23.0")
    implementation("io.insert-koin:koin-ktor:4.1.1")
    implementation("io.insert-koin:koin-logger-slf4j:4.1.1")

    implementation("org.apache.commons:commons-csv:1.14.1")
}
