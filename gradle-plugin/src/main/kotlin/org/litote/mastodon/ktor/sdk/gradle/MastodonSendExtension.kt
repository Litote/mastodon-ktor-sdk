package org.litote.mastodon.ktor.sdk.gradle

import org.gradle.api.provider.Property

/**
 * Extension block for the `mastodonSend` DSL contributed by [MastodonSdkPlugin].
 *
 * Configure it in your build script:
 * ```kotlin
 * mastodonSend {
 *     server = "mastodon.social"
 *     token = providers.gradleProperty("mastodon.token")
 *     visibility = "public"   // optional, default: "unlisted"
 *     language = "fr"         // optional, default: "en"
 * }
 * ```
 *
 * All properties are lazy [Property] wrappers so they participate in Gradle's configuration cache.
 * The class must be abstract so that Gradle can inject managed properties via `ObjectFactory`.
 */
abstract class MastodonSendExtension { // NOSONAR kotlin:S1694

    /** Hostname of the Mastodon instance (e.g. `mastodon.social`). Must not include the scheme. */
    abstract val server: Property<String>

    /** OAuth2 bearer token used to authenticate API requests. */
    abstract val token: Property<String>

    /** Default visibility for posted statuses. Accepted values: `public`, `unlisted`, `private`, `direct`. Defaults to `unlisted`. */
    abstract val visibility: Property<String>

    /** BCP 47 language tag applied to posted statuses (e.g. `en`, `fr`). Defaults to `en`. */
    abstract val language: Property<String>

    init {
        visibility.convention("unlisted")
        language.convention("en")
    }
}
