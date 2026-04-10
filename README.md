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
- Granularity at the operation level: each API operation is a separate dependency/Gradle module
- *alpha stage* SDK for operation composition (e.g. upload an image and post a media status)
- *alpha stage* Gradle plugin for usage with Gradle projects
- *alpha stage* CLI tools for command-line usage
- *alpha stage* MCP server to expose Mastodon tools to AI assistants (Claude Desktop, Claude Code, etc.)

## Using the generated clients

Each API operation has its own module in the [client](client) directory.
Add only what you need:

```kotlin
// POST /api/v1/statuses
implementation("org.litote.mastodon.ktor.sdk:statuses-api-v1-statuses-post-client:<version>")

// POST /api/v2/media
implementation("org.litote.mastodon.ktor.sdk:media-api-v2-media-post-client:<version>")
```

Configure a `ClientConfiguration` with your server and access token:

```kotlin
val config = ClientConfiguration(
    baseUrl = "https://mastodon.example.com/",
    httpClientConfig = {
        install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        defaultRequest {
            header("Authorization", "Bearer $accessToken")
        }
    }
)

val client = StatusesApiV1StatusesPostClient(config)
val response = client.createStatus(TextStatus(status = "Hello Mastodon!"))
```

- All operations available in [client](client) directory
- These clients are generated with [OpenAPI Ktor Client Generator](https://github.com/Litote/openapi-ktor-client-generator?tab=readme-ov-file#openapi-ktor-client-generator)


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
