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

The [API reference (KDoc)](https://litote.github.io/mastodon-ktor-sdk/) documents the latest release; previous
versions are available from the version selector.

## Advanced usage

See [ADVANCED_USAGE.md](ADVANCED_USAGE.md) for the generated clients, and for the *alpha stage* SDK, Gradle plugin, MCP server and CLI.
