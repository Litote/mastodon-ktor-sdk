import com.vanniktech.maven.publish.GradlePublishPlugin
import com.vanniktech.maven.publish.MavenPublishBaseExtension
import org.gradle.plugin.compatibility.compatibility
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(openapi.plugins.gradle.publish)
    id("project-convention")
    id("signing-convention")
    kotlin("jvm")
    id("jacoco")
    alias(openapi.plugins.ktlint)
}

// com.vanniktech.maven.publish is already on the classpath (loaded transitively from the
// convention included build). Applying it here without a version avoids the "already on
// classpath with unknown version" conflict that would occur in the plugins {} block.
apply(plugin = "com.vanniktech.maven.publish")

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
        freeCompilerArgs.addAll("-Xjdk-release=17")
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    compileOnly(gradleApi())
    implementation(openapi.kotlin.gradle.plugin)

    implementation(project(":sdk:send"))
    implementation(project(":sdk:configuration"))
    implementation(project(":client:statuses-api-v1-statuses-post-client"))
    implementation(project(":client:shared-accountsapiv1accountsidstatusesget-4016b7e9"))
    implementation(project(":client:media-api-v2-media-post-client"))
}

val pluginDescription = "Gradle plugin to call Mastodon API"
gradlePlugin {
    plugins {
        create("mastodonSdk") {
            id = "org.litote.mastodon.sdk"
            implementationClass = "org.litote.mastodon.ktor.sdk.gradle.MastodonSdkPlugin"
            displayName = "Gradle Mastodon Sdk plugin"
            description = pluginDescription
            website = "https://github.com/Litote/mastodon-ktor-sdk"
            vcsUrl = "https://github.com/Litote/mastodon-ktor-sdk.git"
            tags.set(listOf("openapi", "mastodon", "client", "sdk"))
            @Suppress("UnstableApiUsage")
            compatibility {
                features {
                    configurationCache = false
                }
            }
        }
    }
}

configure<MavenPublishBaseExtension> {
    configure(GradlePublishPlugin())
    pom {
        description = pluginDescription
    }
}
