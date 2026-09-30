package org.litote.mastodon.ktor.sdk.statusesApiV1StatusesIdQuotesQuotingStatusIdRevokePost.client

import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.http.Headers
import io.ktor.http.encodeURLPathPart
import kotlin.Int
import kotlin.String
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget83730355.model.Status

public interface StatusesApiV1StatusesIdQuotesQuotingStatusIdRevokePostClient {
  /**
   * Revoke a quote post
   */
  public suspend fun postStatusesByIdQuotesByQuotingStatusIdRevoke(id: String, quotingStatusId: String): PostStatusesByIdQuotesByQuotingStatusIdRevokeResponse

  @Serializable
  public sealed class PostStatusesByIdQuotesByQuotingStatusIdRevokeResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class PostStatusesByIdQuotesByQuotingStatusIdRevokeResponseSuccess(
    public val body: Status,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostStatusesByIdQuotesByQuotingStatusIdRevokeResponse() {
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
  public data class PostStatusesByIdQuotesByQuotingStatusIdRevokeResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostStatusesByIdQuotesByQuotingStatusIdRevokeResponse()

  @Serializable
  public data class PostStatusesByIdQuotesByQuotingStatusIdRevokeResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostStatusesByIdQuotesByQuotingStatusIdRevokeResponse()

  @Serializable
  public data class PostStatusesByIdQuotesByQuotingStatusIdRevokeResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostStatusesByIdQuotesByQuotingStatusIdRevokeResponse()

  @Serializable
  public data class PostStatusesByIdQuotesByQuotingStatusIdRevokeResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostStatusesByIdQuotesByQuotingStatusIdRevokeResponse()
}

public fun StatusesApiV1StatusesIdQuotesQuotingStatusIdRevokePostClient(configuration: ClientConfiguration = defaultClientConfiguration): StatusesApiV1StatusesIdQuotesQuotingStatusIdRevokePostClient = DefaultStatusesApiV1StatusesIdQuotesQuotingStatusIdRevokePostClient(configuration)

public class DefaultStatusesApiV1StatusesIdQuotesQuotingStatusIdRevokePostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : StatusesApiV1StatusesIdQuotesQuotingStatusIdRevokePostClient {
  override suspend fun postStatusesByIdQuotesByQuotingStatusIdRevoke(id: String, quotingStatusId: String): StatusesApiV1StatusesIdQuotesQuotingStatusIdRevokePostClient.PostStatusesByIdQuotesByQuotingStatusIdRevokeResponse {
    try {
      val response = configuration.client.post("api/v1/statuses/{id}/quotes/{quoting_status_id}/revoke".replace("/{id}", "/${id.encodeURLPathPart()}").replace("/{quoting_status_id}", "/${quotingStatusId.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> StatusesApiV1StatusesIdQuotesQuotingStatusIdRevokePostClient.PostStatusesByIdQuotesByQuotingStatusIdRevokeResponseSuccess(response.body<Status>(), response.headers)
        401, 403, 404, 429, 503 -> StatusesApiV1StatusesIdQuotesQuotingStatusIdRevokePostClient.PostStatusesByIdQuotesByQuotingStatusIdRevokeResponseFailure401(response.body<Error>(), response.headers)
        410 -> StatusesApiV1StatusesIdQuotesQuotingStatusIdRevokePostClient.PostStatusesByIdQuotesByQuotingStatusIdRevokeResponseFailure410(response.headers)
        422 -> StatusesApiV1StatusesIdQuotesQuotingStatusIdRevokePostClient.PostStatusesByIdQuotesByQuotingStatusIdRevokeResponseFailure(response.body<ValidationError>(), response.headers)
        else -> StatusesApiV1StatusesIdQuotesQuotingStatusIdRevokePostClient.PostStatusesByIdQuotesByQuotingStatusIdRevokeResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return StatusesApiV1StatusesIdQuotesQuotingStatusIdRevokePostClient.PostStatusesByIdQuotesByQuotingStatusIdRevokeResponseUnknownFailure(500)
    }
  }
}
