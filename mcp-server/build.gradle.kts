import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("kotlin-convention")
    alias(libs.plugins.shadow)
}

kotlin {
    // Native binaries only for server/desktop targets
    linuxX64 { binaries { executable { entryPoint = "org.litote.mastodon.ktor.sdk.mcp.main" } } }
    linuxArm64 { binaries { executable { entryPoint = "org.litote.mastodon.ktor.sdk.mcp.main" } } }
    mingwX64 { binaries { executable { entryPoint = "org.litote.mastodon.ktor.sdk.mcp.main" } } }
    if (providers.gradleProperty("appleTargets").map { it.toBoolean() }.getOrElse(true)) {
        macosArm64 { binaries { executable { entryPoint = "org.litote.mastodon.ktor.sdk.mcp.main" } } }
    }

    sourceSets {
        // kotlin-sdk-server does not publish tvos/watchos targets.
        // Use mcpMain as an intermediate source set scoped to supported platforms only.
        val mcpMain by creating {
            dependsOn(commonMain.get())
            dependencies {
                implementation(project(":sdk:send"))
                // ReadSdk exposes Status, Notification and Search via api()
                implementation(project(":sdk:read"))
                implementation(project(":sdk:configuration"))
                // TextStatus, StatusVisibilityEnum, Status, CreateMediaV2Form
                implementation(project(":sdk:api"))
                implementation(libs.mcp.kotlin.sdk.server)
                implementation(libs.kotlinx.io.core)
            }
        }
        // Intermediate source set for all native targets: declares expect fun nativeStdinSource/Sink.
        val serverNativeMain by creating {
            dependsOn(mcpMain)
        }
        // Actual implementations for Unix targets (ssize_t = Long on all 64-bit Unix).
        val serverUnixMain by creating {
            dependsOn(serverNativeMain)
        }

        jvmMain {
            dependsOn(mcpMain)
            dependencies {
                runtimeOnly(libs.slf4j.simple)
            }
        }
        linuxX64Main { dependsOn(serverUnixMain) }
        linuxArm64Main { dependsOn(serverUnixMain) }
        if (providers.gradleProperty("appleTargets").map { it.toBoolean() }.getOrElse(true)) {
            macosArm64Main { dependsOn(serverUnixMain) }
        }
        // mingwX64Main provides its own actual via serverNativeMain expect/actual.
        mingwX64Main { dependsOn(serverNativeMain) }

        jvmTest {
            dependencies {
                implementation(libs.ktor.client.mock)
                implementation(libs.coroutines.test)
            }
        }
    }
}

tasks.named<ShadowJar>("shadowJar") {
    group = "build"
    description = "Assembles a fat JAR containing all dependencies for the Mastodon MCP Server."
    archiveBaseName.set("mastodon-mcp-server")
    archiveClassifier.set("")
    configurations = listOf(project.configurations.getByName("jvmRuntimeClasspath"))
    from(tasks.named("jvmJar"))
    manifest {
        attributes["Main-Class"] = "org.litote.mastodon.ktor.sdk.mcp.MainKt"
    }
    mergeServiceFiles()
}
