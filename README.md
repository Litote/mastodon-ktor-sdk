# Mastodon Ktor SDK

![Plugin Version](https://img.shields.io/gradle-plugin-portal/v/org.litote.mastodon.sdk)
[![KDoc](https://img.shields.io/badge/KDoc-API_Reference-blue)](https://litote.github.io/mastodon-ktor-sdk/)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Litote_mastodon-ktor-sdk&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=Litote_mastodon-ktor-sdk)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=Litote_mastodon-ktor-sdk&metric=coverage)](https://sonarcloud.io/summary/new_code?id=Litote_mastodon-ktor-sdk)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=Litote_mastodon-ktor-sdk&metric=bugs)](https://sonarcloud.io/summary/new_code?id=Litote_mastodon-ktor-sdk)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=Litote_mastodon-ktor-sdk&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=Litote_mastodon-ktor-sdk)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=Litote_mastodon-ktor-sdk&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=Litote_mastodon-ktor-sdk)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=Litote_mastodon-ktor-sdk&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=Litote_mastodon-ktor-sdk)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=Litote_mastodon-ktor-sdk&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=Litote_mastodon-ktor-sdk)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=Litote_mastodon-ktor-sdk&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=Litote_mastodon-ktor-sdk)
[![Apache2 license](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](https://www.apache.org/licenses/LICENSE-2.0)


Kotlin Multiplatform SDK for the [Mastodon API](https://docs.joinmastodon.org/methods/).

- **Group:** `org.litote.mastodon.ktor.sdk`
- **License:** [Apache 2.0](https://www.apache.org/licenses/LICENSE-2.0.txt)

## Features

- Ktor and kotlinx.serialization dependencies — fully KMP compatible
- Generate only the API operations you need from the published OpenAPI spec
- *alpha stage* SDK for operation composition (e.g. upload an image and post a media status)
- *alpha stage* Gradle plugin for usage with Gradle projects
- *alpha stage* CLI tools for command-line usage
- *alpha stage* MCP server to expose Mastodon tools to AI assistants (Claude Desktop, Claude Code, etc.)

## Getting started

See [sdk/README.md](sdk/README.md).

## Advanced Usage: generated clients

### Clients used by the SDK

The `api` artifact contains the generated clients and models of the operations used by the SDKs
(statuses, media, home timeline, notifications, search), in the `org.litote.mastodon.ktor.sdk.api` package:

```kotlin
implementation("org.litote.mastodon.ktor.sdk:api:<version>")
```

```kotlin
val config = ClientConfiguration(
    baseUrl = "https://mastodon.example.com/",
    accessToken = accessToken,
)

val client = StatusesClient(config)
val response = client.createStatus(TextStatus(status = "Hello Mastodon!"))
```

### Generate only the clients you need

The Mastodon OpenAPI spec maintained by this project is published as `org.litote.mastodon.ktor.sdk:openapi`.
Use [OpenAPI Ktor Client Generator](https://github.com/Litote/openapi-ktor-client-generator?tab=readme-ov-file#openapi-ktor-client-generator)
to generate, in your own build, only the operations you use:

```kotlin
// build.gradle.kts
plugins {
    kotlin("multiplatform") // or kotlin("jvm")
    kotlin("plugin.serialization")
    id("org.litote.openapi.ktor.client.generator.gradle") version "<generator version>"
}

val mastodonSpec by configurations.creating

dependencies {
    mastodonSpec("org.litote.mastodon.ktor.sdk:openapi:<version>@json")
}

apiClientGenerator {
    generators {
        create("mastodon") {
            openApiFile = layout.file(provider { mastodonSpec.singleFile })
            // Use your own package: the SDK artifacts already contain org.litote.mastodon.ktor.sdk.api.
            basePackage = "com.example.mastodon"
            allowedPaths.set(
                setOf(
                    "/api/v1/accounts/{id}"
                ),
            )
        }
    }
}
```

The generated code needs `kotlinx-serialization-json`, `kotlinx-coroutines-core`, `kotlin-logging` and the Ktor client
(`core`, `cio`, `content-negotiation`, `serialization-kotlinx-json`, `logging`).
[samples/custom-client](samples/custom-client) is a working example.

Generated types are not interchangeable with the `api` artifact ones: use the SDKs or your own clients for a given
operation, not both.

## *alpha stage* SDK

For operation composition (e.g. upload an image and post a media status).

See [sdk/README.md](sdk/README.md) for usage.

## *alpha stage* Gradle plugin

For projects that build with Gradle, the `gradle-plugin` module provides `sendText` and `sendMedia` tasks that call the SDK directly — no shell invocation needed.

See [gradle-plugin/README.md](gradle-plugin/README.md) for setup and usage.

## *alpha stage* MCP Server

Expose Mastodon tools to any MCP client (Claude Desktop, Claude Code, etc.) via a STDIO transport.

See [mcp-server/README.md](mcp-server/README.md) for setup and usage.

## *alpha stage* CLI

Command-line tools to post statuses to Mastodon.

See [cli/README.md](cli/README.md) for usage.
