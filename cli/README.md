# Mastodon CLI

Command-line tools to post statuses to Mastodon, built on top of `sdk:send`.

## Using the CLI outside this project

Each GitHub Release attaches a standalone fat JAR (`mastodon-cli-<version>.jar`) that bundles
all dependencies. Only Java 17+ is required — no Gradle, no clone.

**1. Download the JAR from the [latest release](https://github.com/Litote/mastodon-ktor-sdk/releases/latest).**

**2. Run it:**

```bash
java -jar mastodon-cli-<version>.jar send-text \
  --server mastodon.social --token <token> "Hello from the terminal!"

java -jar mastodon-cli-<version>.jar send-media \
  --server mastodon.social --token <token> "My caption" photo.jpg "Alt text"
```

**Available commands:**

| Command | Description |
|---|---|
| `send-text` | Post a plain-text status |
| `send-media` | Post a status with media attachments |

> **Tip:** create a shell alias to avoid repeating the `java -jar` prefix:
> ```bash
> alias mastodon-cli='java -jar ~/bin/mastodon-cli-<version>.jar'
> ```

---

Both commands are also runnable via Gradle and require a JVM.

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
| `--simulate` | — | Log what would be posted without actually sending |

Example:

```
--args="--server mastodon.social --token xxxx --visibility public Hello from Kotlin!"
```

Simulate (no network call):

```
--args="--server mastodon.social --token xxxx --simulate Hello from Kotlin!"
```

## SendMedia — post a status with media attachments

```
./gradlew :cli:jvmRun -PmainClass=org.litote.mastodon.ktor.sdk.send.SendMediaMainKt \
  --args="--server mastodon.example.com --token <token> [--visibility unlisted] [--language en] <status> <file1> [desc1] [<file2> [desc2 [<file3> [desc3 [<file4> [desc4]]]]]]"
```

Supports up to 4 media attachments. Each file can optionally be followed by an alt-text description. File vs. description is detected automatically: if the next argument is a readable file it is treated as the next media file, otherwise as a description.

Supported formats: `jpg`, `jpeg`, `png`, `gif`, `webp`, `mp4`, `mov`.

| Option | Default | Description |
|---|---|---|
| `--server` | required | FQDN of the Mastodon instance |
| `--token` | required | OAuth access token |
| `--visibility` | `unlisted` | `public`, `unlisted`, `private`, `direct` |
| `--language` | `en` | ISO 639 language code |
| `--simulate` | — | Log what would be posted without actually sending |

Example:

```
--args="--server mastodon.social --token xxxx 'My caption' photo1.jpg 'Alt text 1' photo2.png"
```

Simulate (no network call, shows attachment paths, alt texts and file sizes):

```
--args="--server mastodon.social --token xxxx --simulate 'My caption' photo1.jpg 'Alt text 1'"
```
