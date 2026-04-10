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
├── mcp-server/                → MCP server exposing Mastodon tools via STDIO transport
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

### `mcp-server`

MCP (Model Context Protocol) server exposing Mastodon operations as MCP tools, usable from Claude Desktop, Claude Code, or any MCP client.

- **Transport:** STDIO (`StdioServerTransport` from `io.modelcontextprotocol:kotlin-sdk-server`)
- **Tools exposed:** `send_text_status` — posts a plain text status, returns the URL
- **Distribution:** fat-jar (JVM, universal) via `shadowJar` + native binaries (linuxX64, linuxArm64, macosArm64, mingwX64)
- **Simulate mode:** set `MASTODON_SIMULATE=true` env var (or `SdkConfiguration.simulate = true`) to return `SendResult.Simulated` without calling the server. Each caller renders the `SimulateInfo` with its own format (`[simulate] ...` lines).
- **Source sets:** custom intermediate sets required because MCP SDK server does not publish tvOS/watchOS targets, and because `ssize_t` width differs between Unix (64-bit) and Windows (32-bit):
  - `mcpMain` (depends on `commonMain`) — server logic, MCP SDK dependency
  - `serverNativeMain` (depends on `mcpMain`) — `main()` entry point + `expect fun nativeStdinSource/Sink()`
  - `serverUnixMain` (depends on `serverNativeMain`) — `actual` POSIX impls for linuxX64, linuxArm64, macosArm64
  - `mingwX64Main` provides its own `actual` impls using Windows `_read`/`_write` (return `Int`)
  - `jvmMain` depends on `mcpMain`
- **`gradle.properties`:** `kotlin.mpp.applyDefaultHierarchyTemplate=false` (custom `dependsOn()` calls)
- **Entry point:** `org.litote.mastodon.ktor.sdk.mcp.main` (reads `MASTODON_SERVER`, `MASTODON_TOKEN`, `MASTODON_VISIBILITY`, `MASTODON_LANGUAGE` env vars)

```json
// Claude Desktop config example (JVM)
{ "mcpServers": { "mastodon": { "command": "java", "args": ["-jar", "/path/to/mastodon-mcp-server.jar"],
    "env": { "MASTODON_SERVER": "mastodon.social", "MASTODON_TOKEN": "..." } } } }
```

### `gradle-plugin`

Gradle plugin (`org.litote.mastodon.sdk`) providing `sendText` and `sendMedia` tasks.
Published to the Gradle Plugin Portal.

---

## Adding a Hand-written Module

1. Create the module directory (e.g. `sdk/mymodule/` or `mymodule/` at root)
2. Add `build.gradle.kts` applying `id("kotlin-convention")`
3. Add `include(":sdk:mymodule")` (or `include(":mymodule")`) to `settings.gradle.kts` **outside** the generated block
