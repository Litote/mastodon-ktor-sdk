package org.litote.mastodon.ktor.sdk.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

class MastodonSdkPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create("mastodonSend", MastodonSendExtension::class.java)
        project.tasks.register("sendText", SendTextTask::class.java) { task ->
            task.server.convention(extension.server)
            task.token.convention(extension.token)
            task.visibility.convention(extension.visibility)
            task.language.convention(extension.language)
        }
        project.tasks.register("sendMedia", SendMediaTask::class.java) { task ->
            task.server.convention(extension.server)
            task.token.convention(extension.token)
            task.visibility.convention(extension.visibility)
            task.language.convention(extension.language)
        }
    }
}
