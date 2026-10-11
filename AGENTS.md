# AGENTS.md — Mastodon Ktor SDK

> **Purpose**: Instructions for AI agents working on this codebase.
> For installation, configuration, and usage, see [`README.md`](README.md).
> For contributing guidelines and project architecture, see [`CONTRIBUTING.md`](CONTRIBUTING.md) — read it before changing modules, publishing or build configuration.
> The project analysis is in [`AI_CONTEXT.md`](AI_CONTEXT.md) (imported by `CLAUDE.md`).

---

## Critical Rules

### NEVER Do

| Category         | Forbidden Actions                                                                |
|------------------|----------------------------------------------------------------------------------|
| **Code**         | Use `!!`, `println`, `runBlocking`, `GlobalScope`                                |
| **Dependencies** | Add/upgrade dependencies without explicit request, change version catalogs       |
| **Security**     | Log secrets/API keys, expose environment variables, commit credentials           |
| **Scope**        | Mass refactors, rename symbols unnecessarily, formatting-only changes            |
| **Deploy**       | Run publishing tasks (`publishAndReleaseToMavenCentral`, `publishPlugins`, `publish*`) or trigger releases manually — releases go through release-please CI |
| **Generated**    | Manually edit any file under `client/` — those are generated from the OpenAPI spec |
| **Publishing**   | Make a published module depend on `:client:*` (not published) — use `:sdk:api`; add published modules or Kotlin targets without checking Maven Central limits (see `CONTRIBUTING.md`) |
| **Commit**       | Commit changes outside a Pull Request context                                    |

### ALWAYS Do

| Category                   | Required Actions                                                                                          |
|----------------------------|-----------------------------------------------------------------------------------------------------------|
| **Validation**             | Run `./gradlew formatKotlin && ./gradlew check` after every iteration (`.md`-only changes excepted)       |
| **Quality Gate**           | Run `./gradlew check jacocoAggregatedReport sonar sonarCheck` before finalizing **every** task — **`sonarCheck` must pass (0 issues, 0 hotspots, gate OK)**. Requires `systemProp.sonar.token` in `~/.gradle/gradle.properties`. If the token is unavailable locally, note it explicitly and let CI validate. This includes `.github/workflows/` YAML files (analysed by Sonar for security). `.md`-only changes do **not** require Sonar. |
| **Testing**                | When you try to fix a bug, start by adding the test and THEN fix the bug. Add tests for all logic changes |
| **Imports**                | Use single imports only                                                                                   |
| **Language**               | Write all code, comments, and documentation in English                                                    |
| **Visibility**             | Prefer `internal` visibility by default                                                                   |
| **Immutability**           | Prefer `val` over `var`, use immutable data structures                                                    |
| **Document**               | User-facing changes → `README.md`; contributor/architecture changes → `CONTRIBUTING.md`; agent-relevant changes → `AI_CONTEXT.md` + `AGENTS.md` |
| **Keep AI doc up-to-date** | Update `AI_CONTEXT.md` when adding/removing domain types, changing public API, or making architectural decisions. Update `AGENTS.md` when rules or workflows change. |

---

## Code Style

### Kotlin Conventions

- Follow [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- 4-space indentation
- Avoid nullable types unless required
- Use sealed classes for finite state models
- Prefer functional programming patterns

### Test Naming

```kotlin
@Test
fun `GIVEN precondition WHEN action THEN expected result`() {
    //...
}
```

---

## Definition of Done

A change is complete when:

- [ ] Validation and Quality Gate (see *ALWAYS Do*) pass, with the exceptions stated there — `check` compiles without warnings and all tests pass
- [ ] No public API is broken
- [ ] Only relevant files are modified
- [ ] Type safety is preserved
- [ ] Architecture boundaries are respected
- [ ] Tests are added for new logic
- [ ] Hand-written modules only — generated files under `client/` and `sdk/api/build/` are not manually modified

---

## Agent Behavior Guidelines

**When generating code:**
- Be minimal — change only what's necessary
- Be conservative — preserve existing patterns
- Be explicit — no hidden side effects
- Preserve type safety and determinism

**When uncertain:**
- Prefer no change over speculative change
- Favor architectural integrity over feature completion
- Explain conflicts with requirements

---

## Resources

- [Mastodon API docs](https://docs.joinmastodon.org/methods/)
- [Ktor Client](https://ktor.io/docs/client-welcome.html)
- [kotlinx.serialization](https://github.com/Kotlin/kotlinx.serialization)
- [openapi-ktor-client-generator](https://github.com/Litote/openapi-ktor-client-generator)
