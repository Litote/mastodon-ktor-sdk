package org.litote.mastodon.ktor.sdk.oembedApiOembedGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import io.ktor.http.Headers
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.model.OEmbedResponse
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError

public interface OembedApiOembedGetClient {
  /**
   * Get OEmbed info as JSON
   */
  public suspend fun getOembed(
    url: String,
    maxheight: Long? = null,
    maxwidth: Long? = 400,
  ): GetOembedResponse

  @Serializable
  public sealed class GetOembedResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class GetOembedResponseSuccess(
    public val body: OEmbedResponse,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetOembedResponse() {
    /**
     * Number of requests permitted per time period
     */
    public val xRateLimitLimit: Int?
      get() = headers["X-RateLimit-Limit"]?.toIntOrNull()

    /**
     * Number of requests you can still make
     */
    public val xRateLimitRemaining: Int?
      get() = headers["X-RateLimit-Remaining"]?.toIntOrNull()

    /**
     * Timestamp when your rate limit will reset
     */
    public val xRateLimitReset: String?
      get() = headers["X-RateLimit-Reset"]
  }

  @Serializable
  public data class GetOembedResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetOembedResponse()

  @Serializable
  public data class GetOembedResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetOembedResponse()

  @Serializable
  public data class GetOembedResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetOembedResponse()

  @Serializable
  public data class GetOembedResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetOembedResponse()
}

public fun OembedApiOembedGetClient(configuration: ClientConfiguration = defaultClientConfiguration): OembedApiOembedGetClient = DefaultOembedApiOembedGetClient(configuration)

public class DefaultOembedApiOembedGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : OembedApiOembedGetClient {
  override suspend fun getOembed(
    url: String,
    maxheight: Long?,
    maxwidth: Long?,
  ): OembedApiOembedGetClient.GetOembedResponse {
    try {
      val response = configuration.client.`get`("api/oembed") {
        url {
          parameters.append("url", url)
          if (maxheight != null) {
            parameters.append("maxheight", maxheight.toString())
          }
          if (maxwidth != null) {
            parameters.append("maxwidth", maxwidth.toString())
          }
        }
      }
      return when (response.status.value) {
        200 -> OembedApiOembedGetClient.GetOembedResponseSuccess(response.body<OEmbedResponse>(), response.headers)
        401, 404, 429, 503 -> OembedApiOembedGetClient.GetOembedResponseFailure401(response.body<Error>(), response.headers)
        410 -> OembedApiOembedGetClient.GetOembedResponseFailure410(response.headers)
        422 -> OembedApiOembedGetClient.GetOembedResponseFailure(response.body<ValidationError>(), response.headers)
        else -> OembedApiOembedGetClient.GetOembedResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return OembedApiOembedGetClient.GetOembedResponseUnknownFailure(500)
    }
  }
}
