package org.litote.mastodon.ktor.sdk.accountsApiV1AccountsIdFeaturedTagsGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import io.ktor.http.Headers
import io.ktor.http.encodeURLPathPart
import kotlin.Int
import kotlin.String
import kotlin.collections.List
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidfeaturedtagsgetB2ed9160.model.FeaturedTag

public interface AccountsApiV1AccountsIdFeaturedTagsGetClient {
  /**
   * Get account's featured tags
   */
  public suspend fun getAccountFeaturedTags(id: String): GetAccountFeaturedTagsResponse

  @Serializable
  public sealed class GetAccountFeaturedTagsResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class GetAccountFeaturedTagsResponseSuccess(
    public val body: List<FeaturedTag>,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetAccountFeaturedTagsResponse() {
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
  public data class GetAccountFeaturedTagsResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetAccountFeaturedTagsResponse()

  @Serializable
  public data class GetAccountFeaturedTagsResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetAccountFeaturedTagsResponse()

  @Serializable
  public data class GetAccountFeaturedTagsResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetAccountFeaturedTagsResponse()

  @Serializable
  public data class GetAccountFeaturedTagsResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetAccountFeaturedTagsResponse()
}

public fun AccountsApiV1AccountsIdFeaturedTagsGetClient(configuration: ClientConfiguration = defaultClientConfiguration): AccountsApiV1AccountsIdFeaturedTagsGetClient = DefaultAccountsApiV1AccountsIdFeaturedTagsGetClient(configuration)

public class DefaultAccountsApiV1AccountsIdFeaturedTagsGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : AccountsApiV1AccountsIdFeaturedTagsGetClient {
  override suspend fun getAccountFeaturedTags(id: String): AccountsApiV1AccountsIdFeaturedTagsGetClient.GetAccountFeaturedTagsResponse {
    try {
      val response = configuration.client.`get`("api/v1/accounts/{id}/featured_tags".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> AccountsApiV1AccountsIdFeaturedTagsGetClient.GetAccountFeaturedTagsResponseSuccess(response.body<List<FeaturedTag>>(), response.headers)
        401, 404, 429, 503 -> AccountsApiV1AccountsIdFeaturedTagsGetClient.GetAccountFeaturedTagsResponseFailure401(response.body<Error>(), response.headers)
        410 -> AccountsApiV1AccountsIdFeaturedTagsGetClient.GetAccountFeaturedTagsResponseFailure410(response.headers)
        422 -> AccountsApiV1AccountsIdFeaturedTagsGetClient.GetAccountFeaturedTagsResponseFailure(response.body<ValidationError>(), response.headers)
        else -> AccountsApiV1AccountsIdFeaturedTagsGetClient.GetAccountFeaturedTagsResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return AccountsApiV1AccountsIdFeaturedTagsGetClient.GetAccountFeaturedTagsResponseUnknownFailure(500)
    }
  }
}
