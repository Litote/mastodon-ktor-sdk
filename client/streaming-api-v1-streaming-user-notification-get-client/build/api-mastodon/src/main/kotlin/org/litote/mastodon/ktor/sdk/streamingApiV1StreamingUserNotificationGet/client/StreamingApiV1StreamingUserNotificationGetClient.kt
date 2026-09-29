package org.litote.mastodon.ktor.sdk.streamingApiV1StreamingUserNotificationGet.client

import io.ktor.client.plugins.sse.ClientSSESession
import io.ktor.client.plugins.sse.sse
import kotlin.Unit
import kotlin.coroutines.cancellation.CancellationException
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration

public interface StreamingApiV1StreamingUserNotificationGetClient {
  /**
   * Watch your notifications
   */
  public suspend fun getStreamingUserNotification(block: suspend ClientSSESession.() -> Unit)
}

public fun StreamingApiV1StreamingUserNotificationGetClient(configuration: ClientConfiguration = defaultClientConfiguration): StreamingApiV1StreamingUserNotificationGetClient = DefaultStreamingApiV1StreamingUserNotificationGetClient(configuration)

public class DefaultStreamingApiV1StreamingUserNotificationGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : StreamingApiV1StreamingUserNotificationGetClient {
  override suspend fun getStreamingUserNotification(block: suspend ClientSSESession.() -> Unit) {
    try {
      configuration.client.sse(urlString = "api/v1/streaming/user/notification") {
        block()
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
    }
  }
}
