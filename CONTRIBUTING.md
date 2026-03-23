# Contributing

## Prerequisites

- JDK 17+
- Gradle 9+

## Coding conventions

### Follow [official kotlin conventions](https://kotlinlang.org/docs/coding-conventions.html)

## Conventional Commits

All PR titles **must** follow the [Conventional Commits](https://www.conventionalcommits.org/) specification.
This is enforced automatically via `amannn/action-semantic-pull-request` in `ci.yml`.

### Branch naming

PR branches should mirror the PR title using the same Conventional Commits type as prefix:

```
<type>/<short-kebab-description>

# Examples:
feat/yaml-content-type-support
fix/enum-null-value-parsing
chore/update-ktor-version
docs/contributing-branch-naming
refactor/split-renderer-module
```

Use the same types as for PR titles (`feat`, `fix`, `perf`, `chore`, `docs`, `test`, `refactor`, `ci`).
The description should be short, lowercase, and hyphen-separated.

| Type | Version bump | When to use |
|------|-------------|-------------|
| `feat:` | Minor (`0.3.0` → `0.4.0`) | New user-facing feature |
| `fix:` | Patch (`0.3.0` → `0.3.1`) | Bug fix |
| `perf:` | Patch | Performance improvement |
| `feat!:` / `BREAKING CHANGE:` | Major (`0.3.0` → `1.0.0`) | Breaking API change |
| `chore:`, `docs:`, `test:`, `refactor:`, `ci:` | No release | Internal changes |

> **Note:** While the major version is `0`, `feat:` commits bump the minor version (not major). This is controlled by `bump-minor-pre-major: true` in `release-please-config.json`.


### Directory structure mirrors package structure

Each **Gradle module** defines its own root package. Following the [Kotlin recommendation](https://kotlinlang.org/docs/coding-conventions.html#directory-structure), the module's root package is the *common root package* and is omitted from the directory path — all source files live directly under `src/main/kotlin/`.

Sub-packages within a module are reflected as subdirectories only if the module itself contains files from multiple packages.

### [Choose good names](https://kotlinlang.org/docs/coding-conventions.html#choose-good-names) for classes

- do not suffix names with `Impl` or `ImplBase`, or `Util`
- use `Sdk` suffix for sdk classes
- use `Configuration` suffix for configuration classes
- for Util classes prefer `Files.kt` to `FileUtils.kt`

### Prefer top-level function to stateless object declaration

### Be consistent

If you use Spec suffix for domain types, use it for all domain types


## Project structure

```
client/          # Generated API clients (one module per endpoint)
  shared/        # Shared Ktor client configuration (ClientConfiguration)
  shared-*/      # Shared model groups (generated)
  *-client/      # One client per API operation (generated)
sdk/
  configuration/ # SdkConfiguration — shared auth/server config
  send/          # SendSdk — high-level SDK to post text and media statuses
cli/             # Command-line tools to post statuses (SendText, SendMedia)
gradle-plugin/   # Gradle plugin (sendText / sendMedia tasks)
convention/      # Gradle convention plugins
src/main/openapi/
  mastodon.json  # Mastodon OpenAPI specification (source of truth)
```

## Code generation

All modules under `client/` are generated from `src/main/openapi/mastodon.json` using the [openapi-ktor-client-generator](https://github.com/Litote/openapi-ktor-client-generator) Gradle plugin (version defined in `gradle/libs.versions.toml`).

To regenerate the clients after updating the spec:

```bash
./gradlew initApiClientSubproject -PopenApiFile=src/main/openapi/mastodon.json -PsplitByClient=true -PbasePackage=org.litote.mastodon.ktor.sdk -PsplitGranularity=BY_TAG_AND_OPERATION -PsharedModelGranularity=SHARED_PER_GROUP -PsubprojectRootDirectory=client
./gradlew generateMastodon
```

The list of generated `include()` entries in `settings.gradle.kts` is maintained between the `// <openapi-ktor-generated-includes>` markers — do not edit that block manually.

## Update dependencies

```bash
# 1. Update version catalog to latest available versions
./gradlew versionCatalogUpdate

# 2. Regenerate dependency verification metadata (MANDATORY after any dependency change)
./gradlew updateVerificationMetadata
```

> ⚠️ **Always run `updateVerificationMetadata` after any dependency upgrade.**
> Skipping this step causes `Dependency verification failed` errors in IntelliJ and for other contributors.

The `updateVerificationMetadata` task rewrites `gradle/verification-metadata.xml` with fresh SHA-256
checksums for all resolved artifacts. It preserves the `trusted-artifacts` rules (sources JARs,
javadoc JARs, `.pom`, `.module` files) which are IDE-only and exempt from checksum verification.

## Convention plugins

Gradle convention plugins are defined in `convention/`:

| Plugin | Purpose |
|---|---|
| `kotlin-convention` | Kotlin Multiplatform, JVM 17, `explicitApi`, ktlint, Maven publishing |
| `project-convention` | Sets `group` and `version` from `gradle.properties` |
| `signing-convention` | GPG signing via `gpg` command |

All modules (generated and hand-written) should apply `kotlin-convention`.

## Adding a new hand-written module

1. Create the module directory (e.g. `sdk/mymodule/` or `mymodule/` at root)
2. Add a `build.gradle.kts` applying `id("kotlin-convention")`
3. Add `include(":sdk:mymodule")` (or `include(":mymodule")`) to `settings.gradle.kts` **outside** the `// <openapi-ktor-generated-includes>` block
