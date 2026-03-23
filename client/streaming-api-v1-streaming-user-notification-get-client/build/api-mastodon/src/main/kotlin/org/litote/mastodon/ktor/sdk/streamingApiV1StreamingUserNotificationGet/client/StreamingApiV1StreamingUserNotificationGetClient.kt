package org.litote.mastodon.ktor.sdk.streamingApiV1StreamingUserNotificationGet.client

import io.ktor.client.plugins.sse.ClientSSESession
import io.ktor.client.plugins.sse.sse
import kotlin.Unit
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration

public class StreamingApiV1StreamingUserNotificationGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Watch your notifications
   */
  public suspend fun getStreamingUserNotification(block: suspend ClientSSESession.() -> Unit) {
    try {
      configuration.client.sse(urlString = "api/v1/streaming/user/notification") {
        block()
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
    }
  }
}
