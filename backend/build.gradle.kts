val ktor_version = "3.0.0"

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
    implementation("io.ktor:ktor-server-core:${ktor_version}")
    implementation("io.ktor:ktor-server-netty:${ktor_version}")
//    implementation("io.ktor:ktor-server-html-builder:${ktor_version}")
    implementation("io.ktor:ktor-server-cors:${ktor_version}")
    implementation("io.ktor:ktor-server-content-negotiation:${ktor_version}")
    implementation("io.ktor:ktor-serialization-kotlinx-json:${ktor_version}")

    implementation("org.xerial:sqlite-jdbc:3.45.2.0")

    implementation("org.slf4j:slf4j-simple:2.0.9")



    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.5.0")
}

repositories {
    mavenCentral()
}

