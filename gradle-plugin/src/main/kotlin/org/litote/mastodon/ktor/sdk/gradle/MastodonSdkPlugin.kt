package org.litote.mastodon.ktor.sdk.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Gradle plugin that registers the [SendTextTask] and [SendMediaTask] tasks and wires them to the
 * `mastodonSend` extension ([MastodonSendExtension]).
 *
 * Apply it in your build script:
 * ```kotlin
 * plugins {
 *     id("org.litote.mastodon.sdk")
 * }
 *
 * mastodonSend {
 *     server = "mastodon.social"
 *     token = providers.gradleProperty("mastodon.token")
 * }
 * ```
 */
class MastodonSdkPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create("mastodonSend", MastodonSendExtension::class.java)
        project.tasks.register("sendText", SendTextTask::class.java) { task ->
            task.server.convention(extension.server)
            task.token.convention(extension.token)
            task.visibility.convention(extension.visibility)
            task.language.convention(extension.language)
            task.simulate.convention(false)
        }
        project.tasks.register("sendMedia", SendMediaTask::class.java) { task ->
            task.server.convention(extension.server)
            task.token.convention(extension.token)
            task.visibility.convention(extension.visibility)
            task.language.convention(extension.language)
            task.simulate.convention(false)
        }
    }
}
