# Mastodon MCP Server

Exposes Mastodon operations as [MCP](https://modelcontextprotocol.io/) tools, usable from any MCP client such as Claude Desktop or Claude Code.

Communication happens over STDIO — the server is launched as a child process by the MCP client.

## Distribution

Two distributions are available from the [latest release](https://github.com/Litote/mastodon-ktor-sdk/releases/latest):

| Distribution | Requires | Release asset |
|---|---|---|
| Fat JAR | Java 17+ | `mastodon-mcp-server-<version>.jar` |
| Native binary (Linux x64) | nothing | `mastodon-mcp-server-linux-x64` |
| Native binary (Linux ARM64) | nothing | `mastodon-mcp-server-linux-arm64` |
| Native binary (macOS ARM64) | nothing | `mastodon-mcp-server-macos-arm64` |
| Native binary (Windows x64) | nothing | `mastodon-mcp-server-windows-x64.exe` |

## Environment variables

| Variable | Required | Default | Description |
|---|---|---|---|
| `MASTODON_SERVER` | yes | — | Hostname of your Mastodon instance (e.g. `mastodon.social`) |
| `MASTODON_TOKEN` | yes | — | OAuth access token |
| `MASTODON_VISIBILITY` | no | `unlisted` | Default visibility: `public`, `unlisted`, `private`, `direct` |
| `MASTODON_LANGUAGE` | no | `en` | Default ISO 639-1 language code |
| `MASTODON_SIMULATE` | no | `true` | Set to `false` to actually send to Mastodon (dry-run by default). Only affects write tools: read tools always query the server |

## Setup

### Claude Desktop

Edit `~/Library/Application Support/Claude/claude_desktop_config.json` (macOS) or `%APPDATA%\Claude\claude_desktop_config.json` (Windows).

**With the fat JAR:**

```json
{
  "mcpServers": {
    "mastodon": {
      "command": "java",
      "args": ["-jar", "/path/to/mastodon-mcp-server-<version>.jar"],
      "env": {
        "MASTODON_SERVER": "mastodon.social",
        "MASTODON_TOKEN": "your-access-token"
      }
    }
  }
}
```

**With the native binary:**

```json
{
  "mcpServers": {
    "mastodon": {
      "command": "/path/to/mastodon-mcp-server",
      "env": {
        "MASTODON_SERVER": "mastodon.social",
        "MASTODON_TOKEN": "your-access-token"
      }
    }
  }
}
```

### Claude Code

Add to your project's `.mcp.json` or to `~/.claude/mcp.json`:

```json
{
  "mcpServers": {
    "mastodon": {
      "command": "java",
      "args": ["-jar", "/path/to/mastodon-mcp-server-<version>.jar"],
      "env": {
        "MASTODON_SERVER": "mastodon.social",
        "MASTODON_TOKEN": "your-access-token"
      }
    }
  }
}
```

## Available tools

### `send_text_status`

Post a plain-text status to Mastodon. Returns the URL of the posted status.

| Parameter | Type | Required | Description |
|---|---|---|---|
| `text` | string | yes | Content of the status |
| `visibility` | string | no | `public`, `unlisted`, `private`, or `direct` (overrides `MASTODON_VISIBILITY`) |
| `language` | string | no | ISO 639-1 language code, e.g. `fr` (overrides `MASTODON_LANGUAGE`) |
| `spoiler_text` | string | no | Content warning shown before the status |
| `in_reply_to_id` | string | no | ID of the status to reply to |

**Example prompt:**

> Post to Mastodon: "Hello from Claude!"

### `send_media_status`

Post a status with 1 to 4 media attachments read from local files (`jpg`, `jpeg`, `png`, `gif`, `webp`, `mp4`, `mov`).
Waits until Mastodon has processed the media (up to 60 seconds) before posting. Returns the URL of the posted status.

| Parameter | Type | Required | Description |
|---|---|---|---|
| `files` | string[] | yes | Absolute paths of 1 to 4 local files |
| `descriptions` | string[] | no | Alt text of each attachment, in the same order as `files` |
| `text` | string | no | Content of the status |
| `visibility` | string | no | `public`, `unlisted`, `private`, or `direct` (overrides `MASTODON_VISIBILITY`) |
| `language` | string | no | ISO 639-1 language code (overrides `MASTODON_LANGUAGE`) |
| `spoiler_text` | string | no | Content warning shown before the status |
| `in_reply_to_id` | string | no | ID of the status to reply to |

### `delete_status`

Delete a status posted by the authenticated user.

| Parameter | Type | Required | Description |
|---|---|---|---|
| `id` | string | yes | ID of the status to delete |

### `get_home_timeline`

Read the most recent statuses of the home timeline as plain text (author, date, text, URL and ID).

| Parameter | Type | Required | Description |
|---|---|---|---|
| `limit` | integer | no | Number of statuses, 1 to 40 (default 20) |

### `get_notifications`

Read the most recent notifications (type, author, date and related status).

| Parameter | Type | Required | Description |
|---|---|---|---|
| `limit` | integer | no | Number of notifications, 1 to 40 (default 20) |

### `search`

Search accounts, statuses and hashtags.

| Parameter | Type | Required | Description |
|---|---|---|---|
| `query` | string | yes | Text to search for |
| `type` | string | no | `accounts`, `statuses`, or `hashtags` (all kinds when omitted) |
| `limit` | integer | no | Maximum number of results per kind, 1 to 40 (default 20) |

## Building from source

```bash
# Fat JAR (JVM)
./gradlew :mcp-server:shadowJar
# → mcp-server/build/libs/mastodon-mcp-server-<version>.jar

# Native binary (Linux x64)
./gradlew :mcp-server:linkReleaseExecutableLinuxX64
# → mcp-server/build/bin/linuxX64/releaseExecutable/mcp-server.kexe
```
