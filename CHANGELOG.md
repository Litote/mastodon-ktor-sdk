# Changelog

## [0.4.0](https://github.com/Litote/mastodon-ktor-sdk/compare/v0.3.0...v0.4.0) (2026-10-09)


### ⚠ BREAKING CHANGES

* do not publish all sub modules and upgrade openapi-ktor-client-generator to 0.9.0

### Features

* do not publish all sub modules and upgrade openapi-ktor-client-generator to 0.9.0 ([037f637](https://github.com/Litote/mastodon-ktor-sdk/commit/037f6371615acc6c6eed1e9202fd7a05404ad6f5))

## [0.3.0](https://github.com/Litote/mastodon-ktor-sdk/compare/v0.2.0...v0.3.0) (2026-09-30)


### ⚠ BREAKING CHANGES

* SendResult has a new MediaProcessingFailure variant, so exhaustive `when` expressions on SendResult must handle it.

### Features

* add ReadSdk, status deletion and new MCP tools ([6fcdd31](https://github.com/Litote/mastodon-ktor-sdk/commit/6fcdd31827c54742bdf2123854aec3a5a621bb8c))
* wait for asynchronous media processing before posting a status ([3e3fe20](https://github.com/Litote/mastodon-ktor-sdk/commit/3e3fe204d3b926188357ffe33e88efc361d96746))


### Bug Fixes

* apply SdkConfiguration defaults in SendSdk and expose actionable error messages ([a5b180d](https://github.com/Litote/mastodon-ktor-sdk/commit/a5b180df53ef6621944057e9eb411734b7baaffc))
* cli build error - "ld.lld: error: duplicate symbol" ([ff3789a](https://github.com/Litote/mastodon-ktor-sdk/commit/ff3789a0a4ec8827187d6b4306451979fa73a6e9))

## [0.2.0](https://github.com/Litote/mastodon-ktor-sdk/compare/v0.1.0...v0.2.0) (2026-04-10)


### Features

* add cli fat jar ([a4799a0](https://github.com/Litote/mastodon-ktor-sdk/commit/a4799a0514f17d12a4acf2d5a2f4be23b9a3734e))
* add mcp server ([350b8a4](https://github.com/Litote/mastodon-ktor-sdk/commit/350b8a49b0a81954cbcdb8e44e38b09c4352dd04))
* move cli to kmp ([32e54b2](https://github.com/Litote/mastodon-ktor-sdk/commit/32e54b28f7d0cf0c75be82cb30180d3b1600af8c))

## [0.1.0](https://github.com/Litote/mastodon-ktor-sdk/compare/v0.0.1...v0.1.0) (2026-03-24)


### Features

* init release ([06221c9](https://github.com/Litote/mastodon-ktor-sdk/commit/06221c9062dc8f6616229c043e4fbd7e2b44d42a))
