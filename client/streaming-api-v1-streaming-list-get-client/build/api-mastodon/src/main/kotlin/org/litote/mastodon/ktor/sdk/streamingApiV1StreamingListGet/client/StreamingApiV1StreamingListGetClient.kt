package org.litote.mastodon.ktor.sdk.streamingApiV1StreamingListGet.client

import io.ktor.client.plugins.sse.ClientSSESession
import io.ktor.client.plugins.sse.sse
import kotlin.String
import kotlin.Unit
import kotlin.coroutines.cancellation.CancellationException
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration

public interface StreamingApiV1StreamingListGetClient {
  /**
   * Watch for list updates
   */
  public suspend fun getStreamingList(list: String, block: suspend ClientSSESession.() -> Unit)
}

public fun StreamingApiV1StreamingListGetClient(configuration: ClientConfiguration = defaultClientConfiguration): StreamingApiV1StreamingListGetClient = DefaultStreamingApiV1StreamingListGetClient(configuration)

public class DefaultStreamingApiV1StreamingListGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : StreamingApiV1StreamingListGetClient {
  override suspend fun getStreamingList(list: String, block: suspend ClientSSESession.() -> Unit) {
    try {
      configuration.client.sse(urlString = "api/v1/streaming/list", request = {
        url {
          parameters.append("list", list)
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
