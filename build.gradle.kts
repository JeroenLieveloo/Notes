plugins {
    kotlin("jvm") version "2.0.0"
    application
}

dependencies {
    implementation("io.ktor:ktor-server-core:3.0.0")
    implementation("io.ktor:ktor-server-netty:3.0.0")
    implementation("io.ktor:ktor-server-html-builder:3.0.0")

    implementation("org.xerial:sqlite-jdbc:3.45.2.0")

    implementation("org.slf4j:slf4j-simple:2.0.9")
}

application {
    mainClass.set("MainKt")
}

repositories {
    mavenCentral()
}

