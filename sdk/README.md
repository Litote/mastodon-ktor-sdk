# SDK

High-level Kotlin Multiplatform SDK for composing Mastodon API operations.
Both `sendText` and `sendMedia` are suspend functions — call them from a coroutine scope.

## Modules

| Module | Description |
|--------|-------------|
| `sdk:configuration` | `SdkConfiguration` — shared auth/server config |
| `sdk:send` | `SendSdk` — post text and media statuses, delete statuses |
| `sdk:read` | `ReadSdk` — read the home timeline and notifications, search |

## Setup

```kotlin
// build.gradle.kts
dependencies {
    implementation("org.litote.mastodon.ktor.sdk:send:<version>")
    implementation("org.litote.mastodon.ktor.sdk:read:<version>") // optional, for ReadSdk
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
    is SendResult.PostFailure -> println(result.errorMessage) // e.g. "Failed to post status: Validation failed: ..."
    is SendResult.UploadFailure -> println(result.errorMessage)
    is SendResult.MediaProcessingFailure -> println(result.errorMessage)
    is SendResult.Simulated -> println("Simulated: ${result.info}")
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
    is SendResult.UploadFailure -> println(result.errorMessage) // e.g. "Failed to upload media: HTTP 410"
    is SendResult.MediaProcessingFailure -> println(result.errorMessage) // e.g. "Media 123 was not processed within the timeout"
    is SendResult.PostFailure -> println(result.errorMessage)
    is SendResult.Simulated -> println("Simulated: ${result.info}")
}
```

Up to 4 attachments are supported. Supported formats: `jpg`, `jpeg`, `png`, `gif`, `webp`, `mp4`, `mov`.

Large files (typically videos) are processed asynchronously by Mastodon. `sendMedia` waits until every
attachment is processed before posting the status, polling every second for up to 60 seconds. Both values
can be tuned with the `mediaPollInterval` and `mediaProcessingTimeout` constructor parameters:

```kotlin
val sdk = SendSdk(
    clientConfig = config.toClientConfiguration(),
    mediaPollInterval = 2.seconds,
    mediaProcessingTimeout = 5.minutes,
)
```

`mediaContentType(fileName)` returns the MIME type matching a file extension, handy to build a `CreateMediaV2FormFile`.

### 5. Delete a status

```kotlin
import org.litote.mastodon.ktor.sdk.send.DeleteResult

when (val result = sdk.deleteStatus("109876543210")) {
    is DeleteResult.Success -> println("Deleted: ${result.status.id}")
    is DeleteResult.Failure -> println(result.errorMessage) // e.g. "Failed to delete status 1098…: Record not found"
    is DeleteResult.Simulated -> println("Would delete ${result.id}")
}
```

### 6. Read the timeline, notifications and search (`sdk:read`)

```kotlin
import org.litote.mastodon.ktor.sdk.read.ReadResult
import org.litote.mastodon.ktor.sdk.read.ReadSdk
import org.litote.mastodon.ktor.sdk.read.SearchType

val reader = ReadSdk(config)

when (val result = reader.homeTimeline(limit = 10)) { // limit: 1 to 40, default 20
    is ReadResult.Success -> result.value.forEach { println("${it.account.acct}: ${it.content}") }
    is ReadResult.Failure -> println(result.errorMessage)
}

reader.notifications(limit = 5)
reader.search("kotlin", type = SearchType.HASHTAGS)
```

Read operations always contact the server, even when `simulate` is `true`.
