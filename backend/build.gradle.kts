plugins {
    kotlin("jvm") version "2.0.0"
    application
    kotlin("plugin.serialization") version "2.0.0"
}

application {
    mainClass.set("Main")
}

dependencies {
    implementation(project(":shared"))
    implementation("io.ktor:ktor-server-core:3.0.0")
    implementation("io.ktor:ktor-server-netty:3.0.0")
    implementation("io.ktor:ktor-server-html-builder:3.0.0")

    implementation("org.xerial:sqlite-jdbc:3.45.2.0")

    implementation("org.slf4j:slf4j-simple:2.0.9")

    implementation("io.ktor:ktor-server-content-negotiation-jvm:2.3.7")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:2.3.7")
}

repositories {
    mavenCentral()
}

