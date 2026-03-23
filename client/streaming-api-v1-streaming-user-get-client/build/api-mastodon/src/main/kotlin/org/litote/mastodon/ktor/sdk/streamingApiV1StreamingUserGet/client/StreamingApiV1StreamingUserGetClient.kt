package org.litote.mastodon.ktor.sdk.streamingApiV1StreamingUserGet.client

import io.ktor.client.plugins.sse.ClientSSESession
import io.ktor.client.plugins.sse.sse
import kotlin.Unit
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration

public class StreamingApiV1StreamingUserGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Watch your home timeline and notifications
   */
  public suspend fun getStreamingUser(block: suspend ClientSSESession.() -> Unit) {
    try {
      configuration.client.sse(urlString = "api/v1/streaming/user") {
        block()
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
    }
  }
}
