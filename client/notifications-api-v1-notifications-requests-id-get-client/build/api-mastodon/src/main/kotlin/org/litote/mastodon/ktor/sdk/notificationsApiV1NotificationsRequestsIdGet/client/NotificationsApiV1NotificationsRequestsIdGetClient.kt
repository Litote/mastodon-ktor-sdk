package org.litote.mastodon.ktor.sdk.notificationsApiV1NotificationsRequestsIdGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
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
import org.litote.mastodon.ktor.sdk.sharedNotificationsapiv1notificationsrequestsget7fa167cd.model.NotificationRequest

public interface NotificationsApiV1NotificationsRequestsIdGetClient {
  /**
   * Get a single notification request
   */
  public suspend fun getNotificationsRequestsById(id: String): GetNotificationsRequestsByIdResponse

  @Serializable
  public sealed class GetNotificationsRequestsByIdResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class GetNotificationsRequestsByIdResponseSuccess(
    public val body: NotificationRequest,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetNotificationsRequestsByIdResponse() {
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
  public data class GetNotificationsRequestsByIdResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetNotificationsRequestsByIdResponse()

  @Serializable
  public data class GetNotificationsRequestsByIdResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetNotificationsRequestsByIdResponse()

  @Serializable
  public data class GetNotificationsRequestsByIdResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetNotificationsRequestsByIdResponse()

  @Serializable
  public data class GetNotificationsRequestsByIdResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetNotificationsRequestsByIdResponse()
}

public fun NotificationsApiV1NotificationsRequestsIdGetClient(configuration: ClientConfiguration = defaultClientConfiguration): NotificationsApiV1NotificationsRequestsIdGetClient = DefaultNotificationsApiV1NotificationsRequestsIdGetClient(configuration)

public class DefaultNotificationsApiV1NotificationsRequestsIdGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : NotificationsApiV1NotificationsRequestsIdGetClient {
  override suspend fun getNotificationsRequestsById(id: String): NotificationsApiV1NotificationsRequestsIdGetClient.GetNotificationsRequestsByIdResponse {
    try {
      val response = configuration.client.`get`("api/v1/notifications/requests/{id}".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> NotificationsApiV1NotificationsRequestsIdGetClient.GetNotificationsRequestsByIdResponseSuccess(response.body<NotificationRequest>(), response.headers)
        401, 404, 429, 503 -> NotificationsApiV1NotificationsRequestsIdGetClient.GetNotificationsRequestsByIdResponseFailure401(response.body<Error>(), response.headers)
        410 -> NotificationsApiV1NotificationsRequestsIdGetClient.GetNotificationsRequestsByIdResponseFailure410(response.headers)
        422 -> NotificationsApiV1NotificationsRequestsIdGetClient.GetNotificationsRequestsByIdResponseFailure(response.body<ValidationError>(), response.headers)
        else -> NotificationsApiV1NotificationsRequestsIdGetClient.GetNotificationsRequestsByIdResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return NotificationsApiV1NotificationsRequestsIdGetClient.GetNotificationsRequestsByIdResponseUnknownFailure(500)
    }
  }
}
