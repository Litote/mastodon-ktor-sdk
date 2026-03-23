package org.litote.mastodon.ktor.sdk.gradle

import org.gradle.api.provider.Property

abstract class MastodonSendExtension {
    abstract val server: Property<String>
    abstract val token: Property<String>
    abstract val visibility: Property<String>
    abstract val language: Property<String>

    init {
        visibility.convention("unlisted")
        language.convention("en")
    }
}
