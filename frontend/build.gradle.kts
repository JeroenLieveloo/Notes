plugins {
    kotlin("multiplatform") version "2.0.0"
    kotlin("plugin.serialization") version "2.0.0"
}

kotlin {
    js(IR) {
        binaries.executable() // marks it as JS app
        browser {
            commonWebpackConfig {
                outputFileName = "frontend.js"
                devServer = devServer?.apply {
                    port = 3000 // frontend dev server port
                }
            }
        }
    }

    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation(project(":shared"))

                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")

                implementation("org.jetbrains.kotlin-wrappers:kotlin-react:18.2.0-pre.686")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react-dom:18.2.0-pre.686")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-emotion:11.11.1-pre.686")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.5.0")
                implementation("org.jetbrains.kotlinx:kotlinx-html-js:0.8.0")



//
//                implementation("io.ktor:ktor-client-core:2.3.7")
//                implementation("io.ktor:ktor-client-js:2.3.7")
//                implementation("io.ktor:ktor-client-content-negotiation:2.3.7")
//                implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.7")
            }
        }
    }
}

repositories {
    mavenCentral()
}
