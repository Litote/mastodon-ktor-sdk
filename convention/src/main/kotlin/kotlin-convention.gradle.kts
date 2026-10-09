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

        if (providers.gradleProperty("appleTargets").map { it.toBoolean() }.getOrElse(true)) {
            // tvOS, watchOS and iosX64 are not published: Maven Central limits the number of published files.
            iosArm64()
            iosSimulatorArm64()
            macosArm64()
        }

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
