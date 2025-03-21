plugins {
    kotlin("jvm") version "2.1.10"
    id("io.ktor.plugin") version "3.0.3"
}

group = "ru.eaglorn"
version = "0.0.1"

application {
    mainClass.set("io.ktor.server.netty.EngineMain")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

val ktor_version = "3.0.3"
val kotlinx_coroutines_version = "1.10.1"
val logback_version = "1.5.16"
val kotlin_version = "2.1.10"

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin_version")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${kotlinx_coroutines_version}")
    implementation("io.ktor:ktor-network:${ktor_version}")
    implementation("io.ktor:ktor-network-tls:${ktor_version}")
    implementation("ch.qos.logback:logback-classic:$logback_version")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(23)
}
