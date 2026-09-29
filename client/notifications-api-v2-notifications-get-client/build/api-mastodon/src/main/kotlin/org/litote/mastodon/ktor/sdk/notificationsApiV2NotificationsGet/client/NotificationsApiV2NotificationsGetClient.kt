package org.litote.mastodon.ktor.sdk.notificationsApiV2NotificationsGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import io.ktor.http.Headers
import kotlin.Boolean
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
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError
import org.litote.mastodon.ktor.sdk.sharedNotificationsapiv1notificationsget1a339ee3.model.NotificationTypeEnum
import org.litote.mastodon.ktor.sdk.sharedNotificationsapiv2notificationsget01d6a505.model.GroupedNotificationsResults

public interface NotificationsApiV2NotificationsGetClient {
  /**
   * Get all grouped notifications
   */
  public suspend fun getNotificationsV2(
    accountId: String? = null,
    excludeTypes: List<NotificationTypeEnum>? = null,
    expandAccounts: String? = null,
    groupedTypes: List<NotificationTypeEnum>? = null,
    includeFiltered: Boolean? = false,
    limit: Long? = 40,
    maxId: String? = null,
    minId: String? = null,
    sinceId: String? = null,
    types: List<NotificationTypeEnum>? = null,
  ): GetNotificationsV2Response

  @Serializable
  public sealed class GetNotificationsV2Response {
    public abstract val headers: Headers
  }

  @Serializable
  public data class GetNotificationsV2ResponseSuccess(
    public val body: GroupedNotificationsResults,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetNotificationsV2Response() {
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
  public data class GetNotificationsV2ResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetNotificationsV2Response()

  @Serializable
  public data class GetNotificationsV2ResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetNotificationsV2Response()

  @Serializable
  public data class GetNotificationsV2ResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetNotificationsV2Response()

  @Serializable
  public data class GetNotificationsV2ResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetNotificationsV2Response()
}

public fun NotificationsApiV2NotificationsGetClient(configuration: ClientConfiguration = defaultClientConfiguration): NotificationsApiV2NotificationsGetClient = DefaultNotificationsApiV2NotificationsGetClient(configuration)

public class DefaultNotificationsApiV2NotificationsGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : NotificationsApiV2NotificationsGetClient {
  override suspend fun getNotificationsV2(
    accountId: String?,
    excludeTypes: List<NotificationTypeEnum>?,
    expandAccounts: String?,
    groupedTypes: List<NotificationTypeEnum>?,
    includeFiltered: Boolean?,
    limit: Long?,
    maxId: String?,
    minId: String?,
    sinceId: String?,
    types: List<NotificationTypeEnum>?,
  ): NotificationsApiV2NotificationsGetClient.GetNotificationsV2Response {
    try {
      val response = configuration.client.`get`("api/v2/notifications") {
        url {
          if (accountId != null) {
            parameters.append("account_id", accountId)
          }
          if (excludeTypes != null) {
            parameters.appendAll("exclude_types", excludeTypes.map { it.serialName() })
          }
          if (expandAccounts != null) {
            parameters.append("expand_accounts", expandAccounts)
          }
          if (groupedTypes != null) {
            parameters.appendAll("grouped_types", groupedTypes.map { it.serialName() })
          }
          if (includeFiltered != null) {
            parameters.append("include_filtered", includeFiltered.toString())
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
          if (sinceId != null) {
            parameters.append("since_id", sinceId)
          }
          if (types != null) {
            parameters.appendAll("types", types.map { it.serialName() })
          }
        }
      }
      return when (response.status.value) {
        200 -> NotificationsApiV2NotificationsGetClient.GetNotificationsV2ResponseSuccess(response.body<GroupedNotificationsResults>(), response.headers)
        401, 404, 429, 503 -> NotificationsApiV2NotificationsGetClient.GetNotificationsV2ResponseFailure401(response.body<Error>(), response.headers)
        410 -> NotificationsApiV2NotificationsGetClient.GetNotificationsV2ResponseFailure410(response.headers)
        422 -> NotificationsApiV2NotificationsGetClient.GetNotificationsV2ResponseFailure(response.body<ValidationError>(), response.headers)
        else -> NotificationsApiV2NotificationsGetClient.GetNotificationsV2ResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return NotificationsApiV2NotificationsGetClient.GetNotificationsV2ResponseUnknownFailure(500)
    }
  }
}
