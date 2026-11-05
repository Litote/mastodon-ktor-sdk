import org.gradle.api.publish.maven.tasks.AbstractPublishToMaven
import org.gradle.kotlin.dsl.findByType
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.gradle.plugins.signing.Sign
import org.jetbrains.dokka.gradle.DokkaExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnPlugin
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnRootExtension
import org.jetbrains.kotlin.gradle.targets.wasm.yarn.WasmYarnPlugin
import org.jetbrains.kotlin.gradle.targets.wasm.yarn.WasmYarnRootExtension
import org.sonarqube.gradle.SonarExtension

plugins {
    id("project-convention")
    alias(libs.plugins.generator)
    alias(libs.plugins.dokka)
    alias(openapi.plugins.version.catalog.update)
    alias(openapi.plugins.ktlint) apply false
    alias(openapi.plugins.sonarqube)
    id("jacoco")
}

// ktor-client-core pins ws to a version affected by GHSA-96hv-2xvq-fx4p.
// Remove these resolutions once Ktor depends on ws >= 8.21.0.
val wsVersion = "8.21.3"
plugins.withType<YarnPlugin> {
    the<YarnRootExtension>().resolution("ws", wsVersion)
}
plugins.withType<WasmYarnPlugin> {
    the<WasmYarnRootExtension>().resolution("ws", wsVersion)
}

// Apply Dokka only to hand-written modules so the 200+ generated client stubs are excluded
// from the aggregated KDoc site.
val dokkaVersioningPlugin = libs.dokka.versioning
configure(
    listOf(
        project(":sdk:configuration"),
        project(":sdk:send"),
        project(":sdk:read"),
        project(":cli"),
        project(":mcp-server"),
        project(":gradle-plugin"),
    ),
) {
    apply(plugin = "org.jetbrains.dokka")
    dependencies {
        "dokkaPlugin"(dokkaVersioningPlugin)
    }
    extensions.configure<DokkaExtension> {
        // Source links target the release tag so that archived versions keep pointing to their own code.
        val modulePath = rootDir.toPath().relativize(projectDir.toPath()).toString()
        dokkaSourceSets.configureEach {
            sourceLink {
                localDirectory.set(projectDir.resolve("src"))
                remoteUrl("https://github.com/Litote/mastodon-ktor-sdk/tree/v${rootProject.version}/$modulePath/src")
                remoteLineSuffix.set("#L")
            }
        }
    }
}

// Aggregate the hand-written modules into a single KDoc publication at the root.
// Running `./gradlew dokkaGeneratePublicationHtml` produces the site in build/dokka/html/.
// Pass `-PdokkaOlderVersionsDir=<dir>` (one sub-directory per previous version) to include the
// archived versions in the version selector — see the `docs` job of release-please.yml.
dependencies {
    dokka(project(":sdk:configuration"))
    dokka(project(":sdk:send"))
    dokka(project(":sdk:read"))
    dokka(project(":cli"))
    dokka(project(":mcp-server"))
    dokka(project(":gradle-plugin"))
    dokkaPlugin(dokkaVersioningPlugin)
}

dokka {
    pluginsConfiguration {
        versioning {
            version.set(project.version.toString())
            providers.gradleProperty("dokkaOlderVersionsDir").orNull?.let {
                olderVersionsDir.set(file(it))
            }
        }
    }
}


apiClientGenerator {
    initSubproject {
        multiplatform.set(true)
        buildScriptTemplate = """
            plugins {
                id("kotlin-convention")
                alias(libs.plugins.generator)
                alias(openapi.plugins.serialization)
            }
            
            mavenPublishing {
                pom {
                    description = "Mastodon client api operation"
                    }
                }
        """.trimIndent()

        generatorConfigExtra = """
            modulesIds.add("UnknownEnumValueModule")
            modulesIds.add("LoggingKotlinModule")
            modulesIds.add("BasicAuthModule")
        """.trimIndent()
    }
}

// Maven Central limits the number of files published per month, so some modules are not published:
// - the per-operation clients under client/ only validate the whole OpenAPI spec; published SDKs rely on :sdk:api,
//   which only contains the operations they use;
// - cli and mcp-server are applications, distributed as fat jars and native binaries on GitHub releases.
subprojects {
    if (path.startsWith(":client:") || path in setOf(":cli", ":mcp-server")) {
        tasks.withType<AbstractPublishToMaven>().configureEach { enabled = false }
        tasks.withType<Sign>().configureEach { enabled = false }
    }
}

tasks.register("addGeneratedSourcesToGit") {
    group = "openapi"
    description = "Stages generated API client sources under client/**/build/api-mastodon and sdk/api/build/api-mastodon into git."
    val generatedRoots = listOf("client", "sdk/api").map { layout.projectDirectory.dir(it).asFile }
    val rootDirectory = layout.projectDirectory.asFile
    doLast {
        generatedRoots
            .flatMap { it.walkTopDown() }
            .filter { it.isDirectory && it.name == "api-mastodon" && it.parentFile?.name == "build" }
            .forEach { dir ->
                ProcessBuilder("git", "add", "-f", dir.absolutePath)
                    .directory(rootDirectory)
                    .start()
                    .waitFor()
            }
    }
}

tasks.configureEach {
    if (name == "generateMastodon") {
        finalizedBy("addGeneratedSourcesToGit")
    }
}

val jacocoAggregatedReport by tasks.registering(JacocoReport::class) {
    dependsOn(subprojects.flatMap { sub ->
        listOfNotNull(sub.tasks.findByName("test"), sub.tasks.findByName("jvmTest"))
    })

    executionData.setFrom(
        subprojects.flatMap { sub ->
            sub.fileTree("${sub.layout.buildDirectory.get()}/jacoco") { include("*.exec") }
        },
    )

    sourceDirectories.setFrom(
        subprojects.flatMap { sub ->
            sub.extensions.findByType(KotlinJvmProjectExtension::class)
                ?.sourceSets?.getByName("main")?.kotlin?.srcDirs
                ?: sub.extensions.findByType(KotlinMultiplatformExtension::class)?.let { kmp ->
                    listOfNotNull(
                        kmp.sourceSets.findByName("commonMain")?.kotlin?.srcDirs,
                        kmp.sourceSets.findByName("jvmMain")?.kotlin?.srcDirs,
                    ).flatten()
                }
                ?: emptyList()
        },
    )

    classDirectories.setFrom(
        subprojects.flatMap { sub ->
            val buildDir = sub.layout.buildDirectory.get()
            listOf(
                sub.fileTree("$buildDir/classes/kotlin/jvm/main") {
                    exclude("**/*\$\$serializer.class")
                },
                sub.fileTree("$buildDir/classes/kotlin/main") {
                    exclude("**/*\$\$serializer.class")
                },
            )
        },
    )

    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

sonar {
    properties {
        property("sonar.projectKey", "Litote_mastodon-ktor-sdk")
        property("sonar.organization", "litote")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.qualitygate.wait", true)
        property(
            "sonar.coverage.jacoco.xmlReportPaths",
            layout.buildDirectory.file("reports/jacoco/jacocoAggregatedReport/jacocoAggregatedReport.xml").get().asFile.absolutePath,
        )
        // Gradle plugin and convention modules are build tooling — no unit tests expected.
        // Native I/O source sets contain platform-specific stdin/stdout wrappers — untestable from the JVM.
        property(
            "sonar.coverage.exclusions",
            "**/gradle-plugin/**,**/convention/**,**/serverNativeMain/**,**/serverUnixMain/**,**/mingwX64Main/**",
        )
    }
}

val aggregatedReportPath: String = layout.buildDirectory
    .file("reports/jacoco/jacocoAggregatedReport/jacocoAggregatedReport.xml")
    .get().asFile.absolutePath

subprojects {
    afterEvaluate {
        extensions.findByType(SonarExtension::class)?.properties {
            property("sonar.coverage.jacoco.xmlReportPaths", aggregatedReportPath)
        }
    }
}


/**
 * Regenerates gradle/verification-metadata.xml after dependency upgrades.
 *
 * Uses --refresh-dependencies to force re-downloading all artifacts (including .module files
 * from the Gradle Plugin Portal) so the generated metadata matches a clean CI environment.
 * Without this flag, locally-cached resolutions can differ from CI and cause verification failures.
 *
 * Run this after every `versionCatalogUpdate` or manual dependency change:
 *   ./gradlew updateVerificationMetadata
 */
tasks.register("updateVerificationMetadata") {
    group = "verification"
    description = "Regenerates gradle/verification-metadata.xml after dependency upgrades. Run after every versionCatalogUpdate."
    notCompatibleWithConfigurationCache("Spawns a Gradle subprocess that writes verification-metadata.xml")
    doLast {
        val result = ProcessBuilder(
            "${rootDir}/gradlew",
            "--write-verification-metadata", "sha256",
            "--refresh-dependencies",
            "dependencies", "check",
        )
            .directory(rootDir)
            .inheritIO()
            .start()
            .waitFor()
        check(result == 0) { "updateVerificationMetadata failed with exit code $result" }
    }
}
