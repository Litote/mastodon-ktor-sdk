package org.litote.mastodon.ktor.sdk.streamingApiV1StreamingDirectGet.client

import io.ktor.client.plugins.sse.ClientSSESession
import io.ktor.client.plugins.sse.sse
import kotlin.Unit
import kotlin.coroutines.cancellation.CancellationException
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration

public interface StreamingApiV1StreamingDirectGetClient {
  /**
   * Watch for direct messages
   */
  public suspend fun getStreamingDirect(block: suspend ClientSSESession.() -> Unit)
}

public fun StreamingApiV1StreamingDirectGetClient(configuration: ClientConfiguration = defaultClientConfiguration): StreamingApiV1StreamingDirectGetClient = DefaultStreamingApiV1StreamingDirectGetClient(configuration)

public class DefaultStreamingApiV1StreamingDirectGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : StreamingApiV1StreamingDirectGetClient {
  override suspend fun getStreamingDirect(block: suspend ClientSSESession.() -> Unit) {
    try {
      configuration.client.sse(urlString = "api/v1/streaming/direct") {
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
