package org.litote.mastodon.ktor.sdk.streamingApiV1StreamingPublicGet.client

import io.ktor.client.plugins.sse.ClientSSESession
import io.ktor.client.plugins.sse.sse
import kotlin.Boolean
import kotlin.Unit
import kotlin.coroutines.cancellation.CancellationException
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration

public interface StreamingApiV1StreamingPublicGetClient {
  /**
   * Watch the federated timeline
   */
  public suspend fun getStreamingPublic(onlyMedia: Boolean? = null, block: suspend ClientSSESession.() -> Unit)
}

public fun StreamingApiV1StreamingPublicGetClient(configuration: ClientConfiguration = defaultClientConfiguration): StreamingApiV1StreamingPublicGetClient = DefaultStreamingApiV1StreamingPublicGetClient(configuration)

public class DefaultStreamingApiV1StreamingPublicGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : StreamingApiV1StreamingPublicGetClient {
  override suspend fun getStreamingPublic(onlyMedia: Boolean?, block: suspend ClientSSESession.() -> Unit) {
    try {
      configuration.client.sse(urlString = "api/v1/streaming/public", request = {
        url {
          if (onlyMedia != null) {
            parameters.append("only_media", onlyMedia.toString())
          }
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
