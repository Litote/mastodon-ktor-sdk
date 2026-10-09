plugins {
    `java-library`
    id("project-convention")
    id("signing-convention")
}

// com.vanniktech.maven.publish is already on the classpath (loaded transitively from the
// convention included build), see gradle-plugin/build.gradle.kts.
apply(plugin = "com.vanniktech.maven.publish")

// Publishes the Mastodon OpenAPI spec so that users can generate only the clients they need with
// openapi-ktor-client-generator. The spec is attached as a `.json` artifact (resolved with the
// `org.litote.mastodon.ktor.sdk:openapi:<version>@json` notation) and also bundled in the jar.
val mastodonSpec = rootProject.layout.projectDirectory.file("src/main/openapi/mastodon.json")

sourceSets.main {
    resources.srcDir(mastodonSpec.asFile.parentFile)
}

configure<PublishingExtension> {
    publications.withType<MavenPublication>().configureEach {
        artifact(mastodonSpec) {
            extension = "json"
        }
    }
}

configure<com.vanniktech.maven.publish.MavenPublishBaseExtension> {
    pom {
        description = "Mastodon OpenAPI specification used to generate the Mastodon Ktor clients"
    }
}
