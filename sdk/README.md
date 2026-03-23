# SDK

High-level Kotlin Multiplatform SDK for composing Mastodon API operations.
Both `sendText` and `sendMedia` are suspend functions — call them from a coroutine scope.

## Modules

| Module | Description |
|--------|-------------|
| `sdk:configuration` | `SdkConfiguration` — shared auth/server config |
| `sdk:send` | `SendSdk` — post text and media statuses |

## Setup

```kotlin
// build.gradle.kts
dependencies {
    implementation("org.litote.mastodon.ktor.sdk:send:<version>")
}
```

## Usage

### 1. Create a configuration

```kotlin
import org.litote.mastodon.ktor.sdk.configuration.SdkConfiguration

val config = SdkConfiguration(
    server = "mastodon.social",
    token = "your-oauth-token",
    visibility = "unlisted", // optional, default: "unlisted"
    language = "en",         // optional, default: "en"
)
```

### 2. Instantiate the SDK

```kotlin
import org.litote.mastodon.ktor.sdk.send.SendSdk

val sdk = SendSdk(config)
```

### 3. Post a text status

```kotlin
import org.litote.mastodon.ktor.sdk.model.TextStatus
import org.litote.mastodon.ktor.sdk.send.SendResult

val result = sdk.sendText(TextStatus(status = "Hello from Kotlin!"))

when (result) {
    is SendResult.Success -> println("Posted: ${result.status.url}")
    is SendResult.PostFailure -> println("Post failed: ${result.response}")
    is SendResult.UploadFailure -> println("File upload failed: $result")
}
```

### 4. Post a status with media attachments

```kotlin
import io.ktor.http.ContentType
import org.litote.mastodon.ktor.sdk.mediaApiV2MediaPost.client.MediaApiV2MediaPostClient.CreateMediaV2Form
import org.litote.mastodon.ktor.sdk.mediaApiV2MediaPost.client.MediaApiV2MediaPostClient.CreateMediaV2FormFile
import org.litote.mastodon.ktor.sdk.model.MediaStatus
import org.litote.mastodon.ktor.sdk.send.SendResult

val attachment = CreateMediaV2Form(
    file = CreateMediaV2FormFile(
        bytes = File("photo.jpg").readBytes(),
        contentType = ContentType.Image.JPEG,
        filename = "photo.jpg",
    ),
    description = "Alt text for accessibility",
)

val result = sdk.sendMedia(
    status = MediaStatus(status = "My photo post", mediaIds = emptyList()),
    attachments = listOf(attachment), // up to 4
)

when (result) {
    is SendResult.Success -> println("Posted: ${result.status.url}")
    is SendResult.UploadFailure -> println("Upload failed: ${result.response}")
    is SendResult.PostFailure -> println("Post failed: ${result.response}")
}
```

Up to 4 attachments are supported. Supported formats: `jpg`, `jpeg`, `png`, `gif`, `webp`, `mp4`, `mov`.
