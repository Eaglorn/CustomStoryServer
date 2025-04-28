plugins {
    kotlin("jvm") version "2.1.20"
    id("io.ktor.plugin") version "3.0.3"
    id("com.google.protobuf") version "0.9.5"
    id("org.springframework.boot") version "3.4.3"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "ru.eaglorn.cs"
version = "0.0.1"

application {
    mainClass.set("ru.eaglorn.cs.ServerApplicationKt")
    applicationName = "CustomStoryServer"
    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

val kotlinVersion = "2.1.20"
val kotlinxCoroutinesVersion = "1.10.2"
val ktorVersion = "3.1.2"
val log4jVersion = "2.24.3"
val protobufVersion = "4.30.2"
val zstdVersion = "1.5.7-2"
val springVersion = "3.4.4"
val eclipseCollections = "11.1.0"

dependencies {
    implementation(project(":CustomStoryLibrary"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutinesVersion")
    implementation("io.ktor:ktor-network:$ktorVersion")
    implementation("io.ktor:ktor-network-tls:$ktorVersion")
    implementation("com.google.protobuf:protobuf-java:$protobufVersion")
    implementation("com.github.luben:zstd-jni:$zstdVersion")
    implementation("org.apache.logging.log4j:log4j-api:${log4jVersion}")
    implementation("org.apache.logging.log4j:log4j-core:${log4jVersion}")
    implementation("org.apache.logging.log4j:log4j-slf4j2-impl:${log4jVersion}")
    implementation("org.springframework.boot:spring-boot-starter:$springVersion") {
        exclude("org.springframework.boot","spring-boot-starter-logging")
    }
    implementation("org.eclipse.collections:eclipse-collections-api:$eclipseCollections")
    implementation("org.eclipse.collections:eclipse-collections:$eclipseCollections")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:$protobufVersion"
    }
}

kotlin {
    jvmToolchain(23)
}
