plugins {
    kotlin("multiplatform") version "2.0.0"
    kotlin("plugin.serialization") version "2.0.0"
}

kotlin {
    js(IR) {
        binaries.executable()
        browser {
            commonWebpackConfig {
                outputFileName = "frontend.js"
            }
        }
    }

    sourceSets {
        val jsMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react:18.2.0-pre.686")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-react-dom:18.2.0-pre.686")
                implementation("org.jetbrains.kotlin-wrappers:kotlin-emotion:11.11.1-pre.686")
//                implementation(npm("react", "18.2.0"))
                implementation(project(":shared"))
            }
        }
    }
}

repositories {
    mavenCentral()
//    google()
}
