package org.litote.mastodon.ktor.sdk.streamingApiV1StreamingListGet.client

import io.ktor.client.plugins.sse.ClientSSESession
import io.ktor.client.plugins.sse.sse
import kotlin.String
import kotlin.Unit
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration

public class StreamingApiV1StreamingListGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Watch for list updates
   */
  public suspend fun getStreamingList(list: String, block: suspend ClientSSESession.() -> Unit) {
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
    catch(e: Exception) {
      configuration.exceptionLogger(e)
    }
  }
}
