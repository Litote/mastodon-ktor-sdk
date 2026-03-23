package org.litote.mastodon.ktor.sdk.streamingApiV1StreamingHashtagGet.client

import io.ktor.client.plugins.sse.ClientSSESession
import io.ktor.client.plugins.sse.sse
import kotlin.String
import kotlin.Unit
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration

public class StreamingApiV1StreamingHashtagGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Watch the public timeline for a hashtag
   */
  public suspend fun getStreamingHashtag(tag: String, block: suspend ClientSSESession.() -> Unit) {
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
    catch(e: Exception) {
      configuration.exceptionLogger(e)
    }
  }
}
