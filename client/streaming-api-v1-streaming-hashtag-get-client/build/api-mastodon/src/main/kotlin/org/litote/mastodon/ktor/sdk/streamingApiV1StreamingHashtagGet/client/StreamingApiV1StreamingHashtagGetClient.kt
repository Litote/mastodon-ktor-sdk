package org.litote.mastodon.ktor.sdk.streamingApiV1StreamingHashtagGet.client

import io.ktor.client.plugins.sse.ClientSSESession
import io.ktor.client.plugins.sse.sse
import kotlin.String
import kotlin.Unit
import kotlin.coroutines.cancellation.CancellationException
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration

public interface StreamingApiV1StreamingHashtagGetClient {
  /**
   * Watch the public timeline for a hashtag
   */
  public suspend fun getStreamingHashtag(tag: String, block: suspend ClientSSESession.() -> Unit)
}

public fun StreamingApiV1StreamingHashtagGetClient(configuration: ClientConfiguration = defaultClientConfiguration): StreamingApiV1StreamingHashtagGetClient = DefaultStreamingApiV1StreamingHashtagGetClient(configuration)

public class DefaultStreamingApiV1StreamingHashtagGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : StreamingApiV1StreamingHashtagGetClient {
  override suspend fun getStreamingHashtag(tag: String, block: suspend ClientSSESession.() -> Unit) {
    try {
      configuration.client.sse(urlString = "api/v1/streaming/hashtag", request = {
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
