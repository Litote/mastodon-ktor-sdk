# gradle-plugin — Mastodon SDK Gradle Plugin

Gradle plugin to post statuses to Mastodon directly from a Gradle build.

Plugin ID: `org.litote.mastodon.sdk`
Published to: [Gradle Plugin Portal](https://plugins.gradle.org/plugin/org.litote.mastodon.sdk)

## Setup

**`build.gradle.kts`**:
```kotlin
plugins {
    id("org.litote.mastodon.sdk") version "<version>"
}

mastodonSend {
    server = "mastodon.social"
    token = providers.environmentVariable("MASTODON_TOKEN").get()
    // visibility = "unlisted"  // default
    // language = "en"          // default
}
```

> Replace `<version>` with the latest release from the [Gradle Plugin Portal](https://plugins.gradle.org/plugin/org.litote.mastodon.sdk).

## Tasks

### `sendText` — post a plain text status

Configure via task properties:

```kotlin
tasks.named<org.litote.mastodon.ktor.sdk.gradle.SendTextTask>("sendText") {
    text = "Hello from Gradle!"
}
```

Or pass `--text` on the command line:

```
./gradlew sendText --text "Hello from Gradle!"
```

Use `--simulate` to validate the configuration and log what would be posted without actually sending:

```
./gradlew sendText --text "Hello from Gradle!" --simulate
```

> **Note:** Gradle's built-in `--dry-run` (`-m`) lists which tasks *would* run but skips all task actions entirely — no validation, no logging. Use `--simulate` for a meaningful preview.

### `sendMedia` — post a status with media attachments

Configure attachments via task properties in `build.gradle.kts`:

```kotlin
tasks.named<org.litote.mastodon.ktor.sdk.gradle.SendMediaTask>("sendMedia") {
    text = "My photo post"  // required
    attach("path/to/photo.jpg", "Alt text for the photo")
    attach("path/to/video.mp4")  // description is optional
}
```

The `--text`, `--attach`, and `--simulate` options are also available on the command line:

```
./gradlew sendMedia --text "My photo post" --attach "path/to/photo.jpg::Alt text for the photo" --attach "path/to/video.mp4"
```

The `--attach` value is either a plain file path or `filePath::alt text` (separated by `::`). The flag can be repeated up to 4 times.

Use `--simulate` to validate the configuration and log what would be posted without actually sending:

```
./gradlew sendMedia --text "My photo post" --attach "path/to/photo.jpg::Alt text" --simulate
```

Up to 4 attachments. Supported formats: `jpg`, `jpeg`, `png`, `gif`, `webp`, `mp4`, `mov`.

## Extension reference

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `server` | `String` | required | FQDN of the Mastodon instance |
| `token` | `String` | required | OAuth access token |
| `visibility` | `String` | `"unlisted"` | `public`, `unlisted`, `private`, `direct` |
| `language` | `String` | `"en"` | ISO 639 language code |

Properties set on the extension are used as defaults for all tasks.
Individual tasks can override any property.

## Task reference

### `sendText`

| Property | Type | Description |
|----------|------|-------------|
| `text` | `String` | Status text to post (required) |
| `simulate` | `Boolean` | Log what would be posted without sending (default: `false`) |

### `sendMedia`

| Property / Method | Type | Description |
|-------------------|------|-------------|
| `text` | `String` | Status text to post (required) |
| `simulate` | `Boolean` | Log what would be posted without sending (default: `false`) |
| `attach(filePath, description?)` | — | Add a media attachment via DSL (up to 4) |
| `--attach "filePath[::alt text]"` | — | Add a media attachment via CLI (repeatable, up to 4) |
