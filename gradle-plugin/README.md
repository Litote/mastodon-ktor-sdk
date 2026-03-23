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

### `sendMedia` — post a status with media attachments

```kotlin
tasks.named<org.litote.mastodon.ktor.sdk.gradle.SendMediaTask>("sendMedia") {
    text = "My photo post"  // required
    attach("path/to/photo.jpg", "Alt text for the photo")
    attach("path/to/video.mp4")  // description is optional
}
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

### `sendMedia`

| Property / Method | Type | Description |
|-------------------|------|-------------|
| `text` | `String` | Status text to post (required) |
| `attach(filePath, description?)` | — | Add a media attachment (up to 4) |
