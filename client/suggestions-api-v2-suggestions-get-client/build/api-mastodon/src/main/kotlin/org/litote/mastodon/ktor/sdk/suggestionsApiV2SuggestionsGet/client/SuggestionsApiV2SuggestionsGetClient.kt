package org.litote.mastodon.ktor.sdk.suggestionsApiV2SuggestionsGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import io.ktor.http.Headers
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.collections.List
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.model.Suggestion
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError

public interface SuggestionsApiV2SuggestionsGetClient {
  /**
   * View follow suggestions (v2)
   */
  public suspend fun getSuggestionsV2(limit: Long? = 40): GetSuggestionsV2Response

  @Serializable
  public sealed class GetSuggestionsV2Response {
    public abstract val headers: Headers
  }

  @Serializable
  public data class GetSuggestionsV2ResponseSuccess(
    public val body: List<Suggestion>,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetSuggestionsV2Response() {
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
  public data class GetSuggestionsV2ResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetSuggestionsV2Response()

  @Serializable
  public data class GetSuggestionsV2ResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetSuggestionsV2Response()

  @Serializable
  public data class GetSuggestionsV2ResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetSuggestionsV2Response()

  @Serializable
  public data class GetSuggestionsV2ResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetSuggestionsV2Response()
}

public fun SuggestionsApiV2SuggestionsGetClient(configuration: ClientConfiguration = defaultClientConfiguration): SuggestionsApiV2SuggestionsGetClient = DefaultSuggestionsApiV2SuggestionsGetClient(configuration)

public class DefaultSuggestionsApiV2SuggestionsGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : SuggestionsApiV2SuggestionsGetClient {
  override suspend fun getSuggestionsV2(limit: Long?): SuggestionsApiV2SuggestionsGetClient.GetSuggestionsV2Response {
    try {
      val response = configuration.client.`get`("api/v2/suggestions") {
        url {
          if (limit != null) {
            parameters.append("limit", limit.toString())
          }
        }
      }
      return when (response.status.value) {
        200 -> SuggestionsApiV2SuggestionsGetClient.GetSuggestionsV2ResponseSuccess(response.body<List<Suggestion>>(), response.headers)
        401, 404, 429, 503 -> SuggestionsApiV2SuggestionsGetClient.GetSuggestionsV2ResponseFailure401(response.body<Error>(), response.headers)
        410 -> SuggestionsApiV2SuggestionsGetClient.GetSuggestionsV2ResponseFailure410(response.headers)
        422 -> SuggestionsApiV2SuggestionsGetClient.GetSuggestionsV2ResponseFailure(response.body<ValidationError>(), response.headers)
        else -> SuggestionsApiV2SuggestionsGetClient.GetSuggestionsV2ResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return SuggestionsApiV2SuggestionsGetClient.GetSuggestionsV2ResponseUnknownFailure(500)
    }
  }
}
