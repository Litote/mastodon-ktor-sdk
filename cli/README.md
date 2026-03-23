# Mastodon CLI

Command-line tools to post statuses to Mastodon, built on top of `sdk:send`.
Both commands are run via Gradle and require a JVM.

## SendText — post a text status

```
./gradlew :cli:jvmRun -PmainClass=org.litote.mastodon.ktor.sdk.send.SendTextMainKt \
  --args="--server mastodon.example.com --token <token> [--visibility unlisted] [--language en] <status text...>"
```

| Option | Default | Description |
|---|---|---|
| `--server` | required | FQDN of the Mastodon instance |
| `--token` | required | OAuth access token |
| `--visibility` | `unlisted` | `public`, `unlisted`, `private`, `direct` |
| `--language` | `en` | ISO 639 language code |

Example:

```
--args="--server mastodon.social --token xxxx --visibility public Hello from Kotlin!"
```

## SendMedia — post a status with media attachments

```
./gradlew :cli:jvmRun -PmainClass=org.litote.mastodon.ktor.sdk.send.SendMediaMainKt \
  --args="--server mastodon.example.com --token <token> [--visibility unlisted] [--language en] <status> <file1> [desc1] [<file2> [desc2 [<file3> [desc3 [<file4> [desc4]]]]]]"
```

Supports up to 4 media attachments. Each file can optionally be followed by an alt-text description. File vs. description is detected automatically: if the next argument is a readable file it is treated as the next media file, otherwise as a description.

Supported formats: `jpg`, `jpeg`, `png`, `gif`, `webp`, `mp4`, `mov`.

Example:

```
--args="--server mastodon.social --token xxxx 'My caption' photo1.jpg 'Alt text 1' photo2.png"
```
