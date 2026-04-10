import groovy.json.JsonSlurper
import org.gradle.kotlin.dsl.findByType
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.sonarqube.gradle.SonarExtension
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

plugins {
    id("project-convention")
    alias(libs.plugins.generator)
    alias(libs.plugins.dokka)
    alias(openapi.plugins.version.catalog.update)
    alias(openapi.plugins.ktlint) apply false
    alias(openapi.plugins.sonarqube)
    id("jacoco")
}

// Apply Dokka only to hand-written modules so the 200+ generated client stubs are excluded
// from the aggregated KDoc site.
configure(
    listOf(
        project(":sdk:configuration"),
        project(":sdk:send"),
        project(":cli"),
        project(":mcp-server"),
        project(":gradle-plugin"),
    ),
) {
    apply(plugin = "org.jetbrains.dokka")
}

// Aggregate the hand-written modules into a single KDoc publication at the root.
// Running `./gradlew dokkaGeneratePublicationHtml` produces the site in build/dokka/html/.
dependencies {
    dokka(project(":sdk:configuration"))
    dokka(project(":sdk:send"))
    dokka(project(":cli"))
    dokka(project(":mcp-server"))
    dokka(project(":gradle-plugin"))
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

tasks.register("addGeneratedSourcesToGit") {
    group = "openapi"
    description = "Stages generated API client sources under client/**/build/api-mastodon into git."
    doLast {
        val clientDir = layout.projectDirectory.dir("client").asFile
        clientDir.walkTopDown()
            .filter { it.isDirectory && it.name == "api-mastodon" && it.parentFile?.name == "build" }
            .forEach { dir ->
                ProcessBuilder("git", "add", "-f", dir.absolutePath)
                    .directory(layout.projectDirectory.asFile)
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

/**
 * Fetches quality gate status and open issues from SonarCloud.
 * Fails the build if the gate is not OK or any issue/hotspot remains.
 *
 * Waits for SonarCloud to finish processing the analysis (async CE task) before checking.
 *
 * Prerequisites: run `./gradlew check jacocoAggregatedReport sonar` first to push the analysis.
 * Token: set `systemProp.sonar.token=<token>` in ~/.gradle/gradle.properties
 *
 * Full quality loop:
 *   ./gradlew check jacocoAggregatedReport sonar sonarCheck
 */
tasks.register("sonarCheck") {
    group = "verification"
    description = "Verifies SonarCloud quality gate: fails if gate != OK, issues > 0, or hotspots > 0."
    mustRunAfter("sonar")
    notCompatibleWithConfigurationCache("Reads sonar/report-task.txt generated at execution time")
    doLast {
        val token = project.providers.systemProperty("sonar.token")
            .orElse(project.providers.environmentVariable("SONAR_TOKEN"))
            .orNull
            ?: error("sonar.token not set. Add systemProp.sonar.token=<token> to ~/.gradle/gradle.properties")

        val projectKey = "Litote_mastodon-ktor-sdk"
        val client = HttpClient.newHttpClient()
        val slurper = JsonSlurper()

        fun fetch(url: String): Any {
            val req = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer $token")
                .GET()
                .build()
            val resp = client.send(req, HttpResponse.BodyHandlers.ofString())
            check(resp.statusCode() == 200) {
                "SonarCloud API error ${resp.statusCode()}: ${resp.body()}"
            }
            return slurper.parseText(resp.body())
        }

        val reportTaskFile = layout.buildDirectory.file("sonar/report-task.txt").get().asFile
        if (reportTaskFile.exists()) {
            val props = java.util.Properties().also { it.load(reportTaskFile.inputStream()) }
            val ceTaskId = props.getProperty("ceTaskId")
            if (ceTaskId != null) {
                logger.lifecycle("⏳ Waiting for SonarCloud analysis (task $ceTaskId) to complete…")
                val maxWaitMs = 120_000L
                val pollIntervalMs = 5_000L
                val start = System.currentTimeMillis()
                while (true) {
                    @Suppress("UNCHECKED_CAST")
                    val taskData = fetch("https://sonarcloud.io/api/ce/task?id=$ceTaskId") as Map<String, Any>
                    @Suppress("UNCHECKED_CAST")
                    val taskStatus = (taskData["task"] as Map<String, Any>)["status"] as String
                    when (taskStatus) {
                        "SUCCESS" -> {
                            logger.lifecycle("✅ Analysis processing complete.")
                            break
                        }
                        "FAILED", "CANCELLED" -> error("SonarCloud analysis task $ceTaskId ended with status: $taskStatus")
                        else -> {
                            check(System.currentTimeMillis() - start < maxWaitMs) {
                                "Timed out waiting for SonarCloud analysis task $ceTaskId (status: $taskStatus)"
                            }
                            Thread.sleep(pollIntervalMs)
                        }
                    }
                }
            }
        }

        @Suppress("UNCHECKED_CAST")
        val qgData = fetch("https://sonarcloud.io/api/qualitygates/project_status?projectKey=$projectKey") as Map<String, Any>
        @Suppress("UNCHECKED_CAST")
        val gateStatus = (qgData["projectStatus"] as Map<String, Any>)["status"] as String

        @Suppress("UNCHECKED_CAST")
        val issuesData = fetch("https://sonarcloud.io/api/issues/search?projectKeys=$projectKey&resolved=false&ps=1") as Map<String, Any>
        val issueCount = (issuesData["total"] as Number).toInt()

        @Suppress("UNCHECKED_CAST")
        val hotspotsData = fetch("https://sonarcloud.io/api/hotspots/search?projectKey=$projectKey&status=TO_REVIEW&ps=1") as Map<String, Any>
        @Suppress("UNCHECKED_CAST")
        val hotspotCount = ((hotspotsData["paging"] as Map<String, Any>)["total"] as Number).toInt()

        logger.lifecycle("")
        logger.lifecycle("╔════════════════════════════════════╗")
        logger.lifecycle("║     SonarCloud Quality Gate        ║")
        logger.lifecycle("╠════════════════════════════════════╣")
        logger.lifecycle("║  Gate    : ${gateStatus.padEnd(24)}║")
        logger.lifecycle("║  Issues  : ${issueCount.toString().padEnd(24)}║")
        logger.lifecycle("║  Hotspots: ${hotspotCount.toString().padEnd(24)}║")
        logger.lifecycle("╚════════════════════════════════════╝")
        logger.lifecycle("")

        // NONE = gate not yet calibrated (no baseline period); ERROR = gate conditions failed.
        // Accepting NONE is safe because issues and hotspots are checked independently above.
        val failures = buildList {
            if (gateStatus != "OK" && gateStatus != "NONE") add("quality gate status is '$gateStatus' (expected OK or NONE)")
            if (issueCount > 0) add("$issueCount unresolved issue(s)")
            if (hotspotCount > 0) add("$hotspotCount unreviewed security hotspot(s)")
        }

        check(failures.isEmpty()) {
            "❌ SonarCloud check FAILED:\n${failures.joinToString("\n") { "  • $it" }}"
        }

        logger.lifecycle("✅ SonarCloud quality gate passed — 0 issues, 0 hotspots.")
    }
}

