package org.litote.mastodon.ktor.sdk.streamingApiV1StreamingHashtagLocalGet.client

import io.ktor.client.plugins.sse.ClientSSESession
import io.ktor.client.plugins.sse.sse
import kotlin.String
import kotlin.Unit
import kotlin.coroutines.cancellation.CancellationException
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration

public interface StreamingApiV1StreamingHashtagLocalGetClient {
  /**
   * Watch the local timeline for a hashtag
   */
  public suspend fun getStreamingHashtagLocal(tag: String, block: suspend ClientSSESession.() -> Unit)
}

public fun StreamingApiV1StreamingHashtagLocalGetClient(configuration: ClientConfiguration = defaultClientConfiguration): StreamingApiV1StreamingHashtagLocalGetClient = DefaultStreamingApiV1StreamingHashtagLocalGetClient(configuration)

public class DefaultStreamingApiV1StreamingHashtagLocalGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : StreamingApiV1StreamingHashtagLocalGetClient {
  override suspend fun getStreamingHashtagLocal(tag: String, block: suspend ClientSSESession.() -> Unit) {
    try {
      configuration.client.sse(urlString = "api/v1/streaming/hashtag/local", request = {
        url {
          parameters.append("tag", tag)
        }
      }
      ) {
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
