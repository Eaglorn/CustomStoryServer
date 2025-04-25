plugins {
    kotlin("jvm") version "2.1.10"
    id("io.ktor.plugin") version "3.0.3"
    id("com.google.protobuf") version "0.9.5"
}

group = "ru.eaglorn"
version = "0.0.1"

application {
    mainClass.set("ru.eaglorn.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

val ktorVersion = "3.0.3"
val kotlinxCoroutinesVersion = "1.10.1"
val logbackVersion = "1.5.16"
val kotlinVersion = "2.1.10"
val protobufVersion = "4.30.2"
val zstdVersion = "1.5.7-2"

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutinesVersion")
    implementation("io.ktor:ktor-network:$ktorVersion")
    implementation("io.ktor:ktor-network-tls:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("com.google.protobuf:protobuf-java:$protobufVersion")
    implementation("com.github.luben:zstd-jni:$zstdVersion")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:$protobufVersion"
    }
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(23)
}
