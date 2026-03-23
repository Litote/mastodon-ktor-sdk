import org.gradle.testing.jacoco.tasks.JacocoReport
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("project-convention")
    id("signing-convention")
    kotlin("multiplatform")
    id("jacoco")
}

plugin("vanniktech.maven.publish")
plugin("ktlint")

kotlin {
    jvm {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
            freeCompilerArgs.addAll("-Xjdk-release=17", "-Xconsistent-data-class-copy-visibility")
        }
    }

        iosArm64()
        iosX64()
        iosSimulatorArm64()
        macosArm64()
        tvosArm64()
        tvosSimulatorArm64()
        watchosArm64()
        watchosSimulatorArm64()

        js {
            browser { testTask { enabled = false } }
            nodejs()
        }

        @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
        wasmJs {
            browser { testTask { enabled = false } }
            nodejs()
        }

    linuxX64()
    linuxArm64()

    mingwX64()

    sourceSets {
        commonTest.dependencies {
            implementation(kotlin("test"))
        }
        commonMain.dependencies {
            api(lib("serialization"))
            api(lib("coroutines"))
            api(lib("logging"))
            api(bundle("ktor"))
        }
    }

    explicitApi()
}
