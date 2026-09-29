package org.litote.mastodon.ktor.sdk.streamingApiV1StreamingPublicRemoteGet.client

import io.ktor.client.plugins.sse.ClientSSESession
import io.ktor.client.plugins.sse.sse
import kotlin.Boolean
import kotlin.Unit
import kotlin.coroutines.cancellation.CancellationException
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration

public interface StreamingApiV1StreamingPublicRemoteGetClient {
  /**
   * Watch for remote statuses
   */
  public suspend fun getStreamingPublicRemote(onlyMedia: Boolean? = null, block: suspend ClientSSESession.() -> Unit)
}

public fun StreamingApiV1StreamingPublicRemoteGetClient(configuration: ClientConfiguration = defaultClientConfiguration): StreamingApiV1StreamingPublicRemoteGetClient = DefaultStreamingApiV1StreamingPublicRemoteGetClient(configuration)

public class DefaultStreamingApiV1StreamingPublicRemoteGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : StreamingApiV1StreamingPublicRemoteGetClient {
  override suspend fun getStreamingPublicRemote(onlyMedia: Boolean?, block: suspend ClientSSESession.() -> Unit) {
    try {
      configuration.client.sse(urlString = "api/v1/streaming/public/remote", request = {
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
