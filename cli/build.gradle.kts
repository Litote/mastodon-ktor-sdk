import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("kotlin-convention")
    alias(libs.plugins.shadow)
}

kotlin {
    linuxX64 { binaries { executable { baseName = "mastodon-cli"; entryPoint = "org.litote.mastodon.ktor.sdk.cli.main" } } }
    linuxArm64 { binaries { executable { baseName = "mastodon-cli"; entryPoint = "org.litote.mastodon.ktor.sdk.cli.main" } } }
    mingwX64 { binaries { executable { baseName = "mastodon-cli"; entryPoint = "org.litote.mastodon.ktor.sdk.cli.main" } } }
    if (providers.gradleProperty("appleTargets").map { it.toBoolean() }.getOrElse(true)) {
        macosArm64 { binaries { executable { baseName = "mastodon-cli"; entryPoint = "org.litote.mastodon.ktor.sdk.cli.main" } } }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":sdk:send"))
            implementation(project(":sdk:configuration"))
            implementation(project(":client:statuses-api-v1-statuses-post-client"))
            implementation(project(":client:media-api-v2-media-post-client"))
            implementation(project(":client:shared-accountsapiv1accountsidstatusesget-4016b7e9"))
            implementation(libs.kotlinx.io.core)
            implementation(libs.clikt)
            implementation(libs.mordant)
        }
        jvmMain.dependencies {
            runtimeOnly(libs.slf4j.simple)
        }
        jvmTest.dependencies {
            implementation(libs.coroutines.test)
        }
    }
}

mavenPublishing {
    pom {
        description = "Mastodon CLI"
    }
}

tasks.named<ShadowJar>("shadowJar") {
    group = "build"
    description = "Assembles a fat JAR containing all dependencies for the Mastodon CLI."
    archiveBaseName.set("mastodon-cli")
    archiveClassifier.set("")
    configurations = listOf(project.configurations.getByName("jvmRuntimeClasspath"))
    from(tasks.named("jvmJar"))
    manifest {
        attributes["Main-Class"] = "org.litote.mastodon.ktor.sdk.cli.MainKt"
    }
    mergeServiceFiles()
}
