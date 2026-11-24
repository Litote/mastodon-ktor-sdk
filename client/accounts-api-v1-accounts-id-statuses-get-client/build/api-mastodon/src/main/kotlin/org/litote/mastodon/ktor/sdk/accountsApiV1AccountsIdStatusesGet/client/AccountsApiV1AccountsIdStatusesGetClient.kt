package org.litote.mastodon.ktor.sdk.accountsApiV1AccountsIdStatusesGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import io.ktor.http.Headers
import io.ktor.http.encodeURLPathPart
import kotlin.Boolean
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Unit
import kotlin.collections.List
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.client.OperationResponse
import org.litote.mastodon.ktor.sdk.client.OperationResponseFailure
import org.litote.mastodon.ktor.sdk.client.OperationResponseSuccess
import org.litote.mastodon.ktor.sdk.client.OperationResponseUnknownFailure
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget83730355.model.Status

public interface AccountsApiV1AccountsIdStatusesGetClient {
  /**
   * Get account's statuses
   */
  public suspend fun getAccountStatuses(
    id: String,
    excludeReblogs: Boolean? = null,
    excludeReplies: Boolean? = null,
    limit: Long? = 20,
    maxId: String? = null,
    minId: String? = null,
    onlyMedia: Boolean? = null,
    pinned: Boolean? = null,
    sinceId: String? = null,
    tagged: String? = null,
  ): GetAccountStatusesResponse

  @Serializable
  public sealed class GetAccountStatusesResponse : OperationResponse<List<Status>> {
    abstract override val headers: Headers
  }

  @Serializable
  public data class GetAccountStatusesResponseSuccess(
    override val body: List<Status>,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetAccountStatusesResponse(),
      OperationResponseSuccess<List<Status>> {
    /**
     * Pagination links for browsing older or newer results. Format: Link: <https://mastodon.example/api/v1/endpoint?max_id=7163058>; rel="next", <https://mastodon.example/api/v1/endpoint?min_id=7275607>; rel="prev". See [RFC 8288](https://www.rfc-editor.org/rfc/rfc8288) for more information.
     */
    public val link: String?
      get() = headers["Link"]

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
  public data class GetAccountStatusesResponseFailure401(
    override val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetAccountStatusesResponse(),
      OperationResponseFailure<Error>

  @Serializable
  public data class GetAccountStatusesResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetAccountStatusesResponse(),
      OperationResponseFailure<Unit> {
    override val body: Unit
      get() = Unit
  }

  @Serializable
  public data class GetAccountStatusesResponseFailure(
    override val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetAccountStatusesResponse(),
      OperationResponseFailure<ValidationError>

  @Serializable
  public data class GetAccountStatusesResponseUnknownFailure(
    override val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetAccountStatusesResponse(),
      OperationResponseUnknownFailure
}

public fun AccountsApiV1AccountsIdStatusesGetClient(configuration: ClientConfiguration = defaultClientConfiguration): AccountsApiV1AccountsIdStatusesGetClient = DefaultAccountsApiV1AccountsIdStatusesGetClient(configuration)

public class DefaultAccountsApiV1AccountsIdStatusesGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : AccountsApiV1AccountsIdStatusesGetClient {
  override suspend fun getAccountStatuses(
    id: String,
    excludeReblogs: Boolean?,
    excludeReplies: Boolean?,
    limit: Long?,
    maxId: String?,
    minId: String?,
    onlyMedia: Boolean?,
    pinned: Boolean?,
    sinceId: String?,
    tagged: String?,
  ): AccountsApiV1AccountsIdStatusesGetClient.GetAccountStatusesResponse {
    try {
      val response = configuration.client.`get`("api/v1/accounts/{id}/statuses".replace("/{id}", "/${id.encodeURLPathPart()}")) {
        url {
          if (excludeReblogs != null) {
            parameters.append("exclude_reblogs", excludeReblogs.toString())
          }
          if (excludeReplies != null) {
            parameters.append("exclude_replies", excludeReplies.toString())
          }
          if (limit != null) {
            parameters.append("limit", limit.toString())
          }
          if (maxId != null) {
            parameters.append("max_id", maxId)
          }
          if (minId != null) {
            parameters.append("min_id", minId)
          }
          if (onlyMedia != null) {
            parameters.append("only_media", onlyMedia.toString())
          }
          if (pinned != null) {
            parameters.append("pinned", pinned.toString())
          }
          if (sinceId != null) {
            parameters.append("since_id", sinceId)
          }
          if (tagged != null) {
            parameters.append("tagged", tagged)
          }
        }
      }
      return when (response.status.value) {
        200 -> AccountsApiV1AccountsIdStatusesGetClient.GetAccountStatusesResponseSuccess(response.body<List<Status>>(), response.headers)
        401, 404, 429, 503 -> AccountsApiV1AccountsIdStatusesGetClient.GetAccountStatusesResponseFailure401(response.body<Error>(), response.headers)
        410 -> AccountsApiV1AccountsIdStatusesGetClient.GetAccountStatusesResponseFailure410(response.headers)
        422 -> AccountsApiV1AccountsIdStatusesGetClient.GetAccountStatusesResponseFailure(response.body<ValidationError>(), response.headers)
        else -> AccountsApiV1AccountsIdStatusesGetClient.GetAccountStatusesResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return AccountsApiV1AccountsIdStatusesGetClient.GetAccountStatusesResponseUnknownFailure(500)
    }
  }
}
