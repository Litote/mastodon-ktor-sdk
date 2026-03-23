# AI Context — Mastodon Ktor SDK

> Codebase analysis for AI agents. Keep this file up-to-date after significant changes.

---

## Project Overview

Kotlin Multiplatform SDK for the [Mastodon API](https://docs.joinmastodon.org/methods/).

- **Group:** `org.litote.mastodon.ktor.sdk`
- **License:** Apache 2.0

---

## Module Structure

```
mastodon-ktor-sdk/
├── src/main/openapi/
│   └── mastodon.json          → Mastodon OpenAPI spec (source of truth for client/)
├── client/                    → Generated API clients (do NOT edit manually)
│   ├── shared/                → Shared ClientConfiguration (generated)
│   ├── shared-*/              → Shared model groups (generated)
│   └── *-client/              → One module per API operation (generated)
├── sdk/
│   ├── configuration/         → SdkConfiguration + toClientConfiguration()
│   └── send/                  → SendSdk — high-level SDK to post text and media statuses
├── cli/                       → Command-line tools (SendText, SendMedia) — JVM entry points
├── gradle-plugin/             → Gradle plugin (sendText / sendMedia tasks)
├── convention/                → Gradle convention plugins
├── build.gradle.kts
└── settings.gradle.kts
```

---

## Convention Plugins (`convention/`)

| Plugin | Purpose |
|---|---|
| `kotlin-convention` | Kotlin Multiplatform, JVM 17, `explicitApi`, ktlint, Maven publishing |
| `project-convention` | Sets `group` and `version` from `gradle.properties` |
| `signing-convention` | GPG signing via `gpg` command |

All modules (generated and hand-written) apply `kotlin-convention`, except `gradle-plugin` which uses `kotlin("jvm")` directly (Gradle plugin constraints).

---

## Hand-written Modules

### `sdk/configuration`

Provides `SdkConfiguration` (server, token, visibility, language) and `toClientConfiguration()` which builds the Ktor `ClientConfiguration` with auth headers and JSON setup.

### `sdk/send`

Provides `SendSdk` — high-level coroutine-based API:
- `sendText(TextStatus)` — posts a plain text status
- `sendMedia(MediaStatus, List<CreateMediaV2Form>)` — uploads attachments then posts a media status

Returns a sealed `SendResult` (`Success`, `PostFailure`, `UploadFailure`).

### `cli`

JVM entry points that wrap `SendSdk`:
- `SendTextMain` — posts a plain text status
- `SendMediaMain` — posts a status with up to 4 media attachments

```bash
./gradlew :cli:jvmRun -PmainClass=org.litote.mastodon.ktor.sdk.send.SendTextMainKt \
  --args="--server mastodon.example.com --token <token> <text>"
```

### `gradle-plugin`

Gradle plugin (`org.litote.mastodon.sdk`) providing `sendText` and `sendMedia` tasks.
Published to the Gradle Plugin Portal.

---

## Adding a Hand-written Module

1. Create the module directory (e.g. `sdk/mymodule/` or `mymodule/` at root)
2. Add `build.gradle.kts` applying `id("kotlin-convention")`
3. Add `include(":sdk:mymodule")` (or `include(":mymodule")`) to `settings.gradle.kts` **outside** the generated block
