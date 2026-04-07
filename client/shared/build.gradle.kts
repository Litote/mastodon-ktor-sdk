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

apiClientGenerator {
    generators {
        create("mastodon") {
            openApiFile = file("../../src/main/openapi/mastodon.json")
            basePackage = "org.litote.mastodon.ktor.sdk"
            splitByClient.set(true)
            splitGranularity.set("BY_TAG_AND_OPERATION")
            sharedModelGranularity.set("SHARED_PER_GROUP")
            modulesIds.add("UnknownEnumValueModule")
            modulesIds.add("LoggingKotlinModule")
            modulesIds.add("BasicAuthModule")
        }
    }
}