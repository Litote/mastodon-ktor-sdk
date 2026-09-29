package org.litote.mastodon.ktor.sdk.streamingApiV1StreamingUserGet.client

import io.ktor.client.plugins.sse.ClientSSESession
import io.ktor.client.plugins.sse.sse
import kotlin.Unit
import kotlin.coroutines.cancellation.CancellationException
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration

public interface StreamingApiV1StreamingUserGetClient {
  /**
   * Watch your home timeline and notifications
   */
  public suspend fun getStreamingUser(block: suspend ClientSSESession.() -> Unit)
}

public fun StreamingApiV1StreamingUserGetClient(configuration: ClientConfiguration = defaultClientConfiguration): StreamingApiV1StreamingUserGetClient = DefaultStreamingApiV1StreamingUserGetClient(configuration)

public class DefaultStreamingApiV1StreamingUserGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : StreamingApiV1StreamingUserGetClient {
  override suspend fun getStreamingUser(block: suspend ClientSSESession.() -> Unit) {
    try {
      configuration.client.sse(urlString = "api/v1/streaming/user") {
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
