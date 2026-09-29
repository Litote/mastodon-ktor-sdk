package org.litote.mastodon.ktor.sdk.notificationsApiV2NotificationsUnreadCountGet.client

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
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError
import org.litote.mastodon.ktor.sdk.sharedNotificationsapiv1notificationsunreadcountget188564c9.model.CountResponse

public interface NotificationsApiV2NotificationsUnreadCountGetClient {
  /**
   * Get the number of unread notifications
   */
  public suspend fun getNotificationsUnreadCountV2(
    accountId: String? = null,
    excludeTypes: List<String>? = null,
    groupedTypes: List<String>? = null,
    limit: Long? = 100,
    types: List<String>? = null,
  ): GetNotificationsUnreadCountV2Response

  @Serializable
  public sealed class GetNotificationsUnreadCountV2Response {
    public abstract val headers: Headers
  }

  @Serializable
  public data class GetNotificationsUnreadCountV2ResponseSuccess(
    public val body: CountResponse,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetNotificationsUnreadCountV2Response() {
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
  public data class GetNotificationsUnreadCountV2ResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetNotificationsUnreadCountV2Response()

  @Serializable
  public data class GetNotificationsUnreadCountV2ResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetNotificationsUnreadCountV2Response()

  @Serializable
  public data class GetNotificationsUnreadCountV2ResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetNotificationsUnreadCountV2Response()

  @Serializable
  public data class GetNotificationsUnreadCountV2ResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetNotificationsUnreadCountV2Response()
}

public fun NotificationsApiV2NotificationsUnreadCountGetClient(configuration: ClientConfiguration = defaultClientConfiguration): NotificationsApiV2NotificationsUnreadCountGetClient = DefaultNotificationsApiV2NotificationsUnreadCountGetClient(configuration)

public class DefaultNotificationsApiV2NotificationsUnreadCountGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : NotificationsApiV2NotificationsUnreadCountGetClient {
  override suspend fun getNotificationsUnreadCountV2(
    accountId: String?,
    excludeTypes: List<String>?,
    groupedTypes: List<String>?,
    limit: Long?,
    types: List<String>?,
  ): NotificationsApiV2NotificationsUnreadCountGetClient.GetNotificationsUnreadCountV2Response {
    try {
      val response = configuration.client.`get`("api/v2/notifications/unread_count") {
        url {
          if (accountId != null) {
            parameters.append("account_id", accountId)
          }
          if (excludeTypes != null) {
            parameters.appendAll("exclude_types", excludeTypes)
          }
          if (groupedTypes != null) {
            parameters.appendAll("grouped_types", groupedTypes)
          }
          if (limit != null) {
            parameters.append("limit", limit.toString())
          }
          if (types != null) {
            parameters.appendAll("types", types)
          }
        }
      }
      return when (response.status.value) {
        200 -> NotificationsApiV2NotificationsUnreadCountGetClient.GetNotificationsUnreadCountV2ResponseSuccess(response.body<CountResponse>(), response.headers)
        401, 404, 429, 503 -> NotificationsApiV2NotificationsUnreadCountGetClient.GetNotificationsUnreadCountV2ResponseFailure401(response.body<Error>(), response.headers)
        410 -> NotificationsApiV2NotificationsUnreadCountGetClient.GetNotificationsUnreadCountV2ResponseFailure410(response.headers)
        422 -> NotificationsApiV2NotificationsUnreadCountGetClient.GetNotificationsUnreadCountV2ResponseFailure(response.body<ValidationError>(), response.headers)
        else -> NotificationsApiV2NotificationsUnreadCountGetClient.GetNotificationsUnreadCountV2ResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return NotificationsApiV2NotificationsUnreadCountGetClient.GetNotificationsUnreadCountV2ResponseUnknownFailure(500)
    }
  }
}
