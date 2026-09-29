package org.litote.mastodon.ktor.sdk.suggestionsApiV1SuggestionsGet.client

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
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersgetB7d593a6.model.Account
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError

public interface SuggestionsApiV1SuggestionsGetClient {
  /**
   * View follow suggestions (v1)
   */
  public suspend fun getSuggestions(limit: Long? = 40): GetSuggestionsResponse

  @Serializable
  public sealed class GetSuggestionsResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class GetSuggestionsResponseSuccess(
    public val body: List<Account>,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetSuggestionsResponse() {
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
  public data class GetSuggestionsResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetSuggestionsResponse()

  @Serializable
  public data class GetSuggestionsResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetSuggestionsResponse()

  @Serializable
  public data class GetSuggestionsResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetSuggestionsResponse()

  @Serializable
  public data class GetSuggestionsResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetSuggestionsResponse()
}

public fun SuggestionsApiV1SuggestionsGetClient(configuration: ClientConfiguration = defaultClientConfiguration): SuggestionsApiV1SuggestionsGetClient = DefaultSuggestionsApiV1SuggestionsGetClient(configuration)

public class DefaultSuggestionsApiV1SuggestionsGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : SuggestionsApiV1SuggestionsGetClient {
  override suspend fun getSuggestions(limit: Long?): SuggestionsApiV1SuggestionsGetClient.GetSuggestionsResponse {
    try {
      val response = configuration.client.`get`("api/v1/suggestions") {
        url {
          if (limit != null) {
            parameters.append("limit", limit.toString())
          }
        }
      }
      return when (response.status.value) {
        200 -> SuggestionsApiV1SuggestionsGetClient.GetSuggestionsResponseSuccess(response.body<List<Account>>(), response.headers)
        401, 404, 429, 503 -> SuggestionsApiV1SuggestionsGetClient.GetSuggestionsResponseFailure401(response.body<Error>(), response.headers)
        410 -> SuggestionsApiV1SuggestionsGetClient.GetSuggestionsResponseFailure410(response.headers)
        422 -> SuggestionsApiV1SuggestionsGetClient.GetSuggestionsResponseFailure(response.body<ValidationError>(), response.headers)
        else -> SuggestionsApiV1SuggestionsGetClient.GetSuggestionsResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return SuggestionsApiV1SuggestionsGetClient.GetSuggestionsResponseUnknownFailure(500)
    }
  }
}
