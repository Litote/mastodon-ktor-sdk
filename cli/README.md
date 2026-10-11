# Mastodon CLI

Command-line tools to post statuses to Mastodon, built on top of `sdk:send`.

## Using the CLI outside this project

Download from the [latest release](https://github.com/Litote/mastodon-ktor-sdk/releases/latest):

| Distribution | Requires | Release asset |
|---|---|---|
| Fat JAR | Java 17+ | `mastodon-cli-<version>.jar` |
| Native binary (Linux x64) | nothing | `mastodon-cli-linux-x64` |
| Native binary (Linux ARM64) | nothing | `mastodon-cli-linux-arm64` |
| Native binary (macOS ARM64) | nothing | `mastodon-cli-macos-arm64` |
| Native binary (Windows x64) | nothing | `mastodon-cli-windows-x64.exe` |

**Run it:**

```bash
# Native binary
mastodon-cli-linux-x64 send-text \
  --server mastodon.social --token <token> "Hello from the terminal!"

# Fat JAR
java -jar mastodon-cli-<version>.jar send-text \
  --server mastodon.social --token <token> "Hello from the terminal!"
```

**Available commands:**

| Command | Description |
|---|---|
| `send-text` | Post a plain-text status |
| `send-media` | Post a status with media attachments |

> **Tip (fat JAR):** create a shell alias to avoid repeating the `java -jar` prefix:
> ```bash
> alias mastodon-cli='java -jar ~/bin/mastodon-cli-<version>.jar'
> ```

---

## Building from source

```bash
# Fat JAR → cli/build/libs/mastodon-cli-<version>.jar
./gradlew :cli:shadowJar
java -jar cli/build/libs/mastodon-cli-<version>.jar send-text --server mastodon.social --token <token> "Hello!"

# Native binary (Linux x64) → cli/build/bin/linuxX64/releaseExecutable/mastodon-cli.kexe
./gradlew :cli:linkReleaseExecutableLinuxX64
```

The examples below use `mastodon-cli` for any of these distributions.

## send-text — post a text status

```
mastodon-cli send-text --server mastodon.example.com --token <token> [--visibility unlisted] [--language en] [--simulate] <status text...>
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
mastodon-cli send-text --server mastodon.social --token xxxx --visibility public Hello from Kotlin!
```

Simulate (no network call):

```
mastodon-cli send-text --server mastodon.social --token xxxx --simulate Hello from Kotlin!
```

## send-media — post a status with media attachments

```
mastodon-cli send-media --server mastodon.example.com --token <token> [--visibility unlisted] [--language en] [--simulate] <status> <file1> [desc1] [<file2> [desc2 [<file3> [desc3 [<file4> [desc4]]]]]]
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
mastodon-cli send-media --server mastodon.social --token xxxx 'My caption' photo1.jpg 'Alt text 1' photo2.png
```

Simulate (no network call, shows attachment paths, alt texts and file sizes):

```
mastodon-cli send-media --server mastodon.social --token xxxx --simulate 'My caption' photo1.jpg 'Alt text 1'
```
