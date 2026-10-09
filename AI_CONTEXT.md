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
├── client/                    → Generated API clients (do NOT edit manually) — NOT published
│   ├── shared/                → Shared ClientConfiguration (generated)
│   ├── shared-*/              → Shared model groups (generated)
│   └── *-client/              → One module per API operation (generated)
├── openapi/                   → Publishes mastodon.json (artifact `openapi`, `@json` notation)
├── samples/custom-client/     → Not published — example/test of user-side filtered generation
├── sdk/
│   ├── api/                   → Generated clients used by the SDKs (allowedPaths filter, package `org.litote.mastodon.ktor.sdk.api`)
│   ├── configuration/         → SdkConfiguration + toClientConfiguration()
│   ├── send/                  → SendSdk — high-level SDK to post text and media statuses, delete statuses
│   └── read/                  → ReadSdk — home timeline, notifications, search
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

All modules (generated and hand-written) apply `kotlin-convention`, except `gradle-plugin` which uses `kotlin("jvm")` directly (Gradle plugin constraints), `openapi` (`java-library`, publishes the spec) and `samples/*` (`kotlin("jvm")`, not published).

---

## Publishing (Maven Central limits)

Maven Central limits files / size / releases per month and per organization. Hence:

- `:client:*` modules are not published (publish + sign tasks disabled in root `build.gradle.kts`); they only validate the spec.
  Published modules must depend on `:sdk:api`, never on `:client:*`.
- `:cli` and `:mcp-server` are not published to Maven Central either (same mechanism) — GitHub releases only (fat jars + native binaries).
- `:sdk:api` is generated (single module, no split) with `allowedPaths` = operations used by the SDKs: clients are per tag
  (`StatusesClient`, `MediaClient`, `TimelinesClient`, `NotificationsClient`, `SearchClient`), models in `org.litote.mastodon.ktor.sdk.api.model`.
  To use a new operation in an SDK, add its path there.
- Users generate other operations themselves from the `openapi` artifact (README procedure, `samples/custom-client`).
  With generator 0.8.0, `allowedPaths` must include `/api/v1/statuses` (its oneOf request/response models are generated even when filtered out).
- Only md5/sha1 checksums, none for `.asc` (`gradle.properties`). No `iosX64` / tvOS / watchOS targets.
- Release/snapshot workflows publish explicit module lists (Apple job: `:sdk:*` only).

## Hand-written Modules

### `sdk/configuration`

Provides `SdkConfiguration` (server, token, visibility, language) and `toClientConfiguration()` which builds the Ktor `ClientConfiguration` with auth headers and JSON setup.

### `sdk/send`

Provides `SendSdk` — high-level coroutine-based API:
- `sendText(TextStatus)` — posts a plain text status
- `sendMedia(MediaStatus, List<CreateMediaV2Form>)` — uploads attachments then posts a media status

Returns a sealed `SendResult` (`Success`, `PostFailure`, `UploadFailure`, `MediaProcessingFailure`, `Simulated`).
`PostFailure` / `UploadFailure` / `MediaProcessingFailure` expose `errorMessage` (server `error` + `error_description`, or `HTTP <code>`); callers (MCP, CLI, Gradle plugin) render it instead of generic messages.

`SdkConfiguration.visibility` / `language` are applied by `SendSdk` itself (`defaultVisibility` / `defaultLanguage` constructor params) to statuses that do not set their own value — callers must not copy them into the status. Unknown visibility → `null` (server account default).

`sendMedia` waits for asynchronous media processing: when `POST /api/v2/media` returns an attachment with `url == null`, it polls `GET /api/v1/media/{id}` (`media-api-v1-media-id-get-client`) every `mediaPollInterval` (default 1s) until 200, within `mediaProcessingTimeout` (default 60s) using `withTimeoutOrNull`. Any other response or timeout → `MediaProcessingFailure(mediaId, response?)` (`null` = timeout) and the status is not posted. Tests driving this use a `MockEngine` whose `dispatcher` is a `StandardTestDispatcher(testScheduler)` so delays and timeouts run in virtual time.

`deleteStatus(id)` returns a sealed `DeleteResult` (`Success`, `Failure` with `errorMessage`, `Simulated`). `mediaContentType(fileName)` is the shared extension → MIME mapping used by the CLI, the Gradle plugin and the MCP server.

### `sdk/read`

Provides `ReadSdk` (built from `SdkConfiguration` or `ClientConfiguration`):
- `homeTimeline(limit)`, `notifications(limit)` — `limit` in `1..MAX_READ_LIMIT` (40), default `DEFAULT_READ_LIMIT` (20)
- `search(query, type: SearchType?, limit)`

Returns a sealed `ReadResult<T>` (`Success(value)`, `Failure(errorMessage)`). A 206 on the home timeline (feed being regenerated) is a `Failure`. Generated clients are declared with `api()` since their `Status` / `Notification` / `Search` types are part of the public API. Reads never honour `simulate`.

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
- **Tools exposed:** `send_text_status`, `send_media_status` (local files, 1–4), `delete_status`, `get_home_timeline`, `get_notifications`, `search`. Registration is in `MastodonMcpServer.kt`; each tool delegates to an `internal suspend fun handleX(args, fn)` in `ToolHandlers.kt` (testable without transport); plain-text rendering of statuses/notifications/search (HTML → text) is in `TextRendering.kt`. Read tools ignore simulate mode.
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
