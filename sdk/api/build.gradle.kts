plugins {
    id("kotlin-convention")
    alias(libs.plugins.generator)
    alias(openapi.plugins.serialization)
}

mavenPublishing {
    pom {
        description = "Mastodon API clients used by the Mastodon SDKs"
    }
}

apiClientGenerator {
    generators {
        create("mastodon") {
            openApiFile = file("../../src/main/openapi/mastodon.json")
            basePackage = "org.litote.mastodon.ktor.sdk.api"
            allowedPaths.set(
                setOf(
                    "/api/v1/statuses",
                    "/api/v1/statuses/{id}",
                    "/api/v2/media",
                    "/api/v1/media/{id}",
                    "/api/v1/timelines/home",
                    "/api/v1/notifications",
                    "/api/v2/search",
                ),
            )
            modulesIds.add("UnknownEnumValueModule")
            modulesIds.add("LoggingKotlinModule")
            modulesIds.add("BasicAuthModule")
        }
    }
}
