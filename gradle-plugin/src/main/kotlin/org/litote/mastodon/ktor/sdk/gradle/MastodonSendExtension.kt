package org.litote.mastodon.ktor.sdk.gradle

import org.gradle.api.provider.Property

// Gradle requires an abstract class (not interface) to inject managed properties via ObjectFactory.
abstract class MastodonSendExtension { // NOSONAR kotlin:S1694
    abstract val server: Property<String>
    abstract val token: Property<String>
    abstract val visibility: Property<String>
    abstract val language: Property<String>

    init {
        visibility.convention("unlisted")
        language.convention("en")
    }
}
