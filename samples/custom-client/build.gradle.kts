import org.jetbrains.kotlin.gradle.dsl.JvmTarget

// Executable example of the "generate only the clients you need" procedure documented in README.md.
// Not published: it checks that a client generated from the Mastodon spec with its own base package
// compiles and runs next to the published SDKs without class name collisions.
plugins {
    id("project-convention")
    kotlin("jvm")
    alias(libs.plugins.generator)
    alias(openapi.plugins.serialization)
    alias(openapi.plugins.ktlint)
}

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

apiClientGenerator {
    generators {
        create("mastodon") {
            // Users resolve the spec from Maven Central instead: org.litote.mastodon.ktor.sdk:openapi:<version>@json
            openApiFile = rootProject.layout.projectDirectory.file("src/main/openapi/mastodon.json")
            basePackage = "org.litote.mastodon.ktor.sdk.sample"
            allowedPaths.set(setOf("/api/v1/accounts/{id}"))
            modulesIds.add("UnknownEnumValueModule")
            modulesIds.add("LoggingKotlinModule")
        }
    }
}

dependencies {
    implementation(openapi.serialization)
    implementation(openapi.coroutines)
    implementation(openapi.logging)
    implementation(openapi.bundles.ktor)

    testImplementation(project(":sdk:send"))
    testImplementation(project(":sdk:configuration"))
    testImplementation(kotlin("test"))
    testImplementation(libs.ktor.client.mock)
    testImplementation(libs.coroutines.test)
}

sonar {
    // Only generated code and a smoke test: nothing to analyse.
    isSkipProject = true
}
