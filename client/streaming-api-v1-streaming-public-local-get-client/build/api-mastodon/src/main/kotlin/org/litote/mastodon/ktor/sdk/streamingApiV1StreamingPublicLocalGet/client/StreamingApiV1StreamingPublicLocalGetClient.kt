package org.litote.mastodon.ktor.sdk.streamingApiV1StreamingPublicLocalGet.client

import io.ktor.client.plugins.sse.ClientSSESession
import io.ktor.client.plugins.sse.sse
import kotlin.Boolean
import kotlin.Unit
import kotlin.coroutines.cancellation.CancellationException
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration

public interface StreamingApiV1StreamingPublicLocalGetClient {
  /**
   * Watch the local timeline
   */
  public suspend fun getStreamingPublicLocal(onlyMedia: Boolean? = null, block: suspend ClientSSESession.() -> Unit)
}

public fun StreamingApiV1StreamingPublicLocalGetClient(configuration: ClientConfiguration = defaultClientConfiguration): StreamingApiV1StreamingPublicLocalGetClient = DefaultStreamingApiV1StreamingPublicLocalGetClient(configuration)

public class DefaultStreamingApiV1StreamingPublicLocalGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : StreamingApiV1StreamingPublicLocalGetClient {
  override suspend fun getStreamingPublicLocal(onlyMedia: Boolean?, block: suspend ClientSSESession.() -> Unit) {
    try {
      configuration.client.sse(urlString = "api/v1/streaming/public/local", request = {
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
