package org.litote.mastodon.ktor.sdk.notificationsApiV1NotificationsRequestsIdAcceptPost.client

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

public interface NotificationsApiV1NotificationsRequestsIdAcceptPostClient {
  /**
   * Accept a single notification request
   */
  public suspend fun postNotificationsRequestsByIdAccept(id: String): PostNotificationsRequestsByIdAcceptResponse

  @Serializable
  public sealed class PostNotificationsRequestsByIdAcceptResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class PostNotificationsRequestsByIdAcceptResponseSuccess(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostNotificationsRequestsByIdAcceptResponse() {
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
  public data class PostNotificationsRequestsByIdAcceptResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostNotificationsRequestsByIdAcceptResponse()

  @Serializable
  public data class PostNotificationsRequestsByIdAcceptResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostNotificationsRequestsByIdAcceptResponse()

  @Serializable
  public data class PostNotificationsRequestsByIdAcceptResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostNotificationsRequestsByIdAcceptResponse()

  @Serializable
  public data class PostNotificationsRequestsByIdAcceptResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostNotificationsRequestsByIdAcceptResponse()
}

public fun NotificationsApiV1NotificationsRequestsIdAcceptPostClient(configuration: ClientConfiguration = defaultClientConfiguration): NotificationsApiV1NotificationsRequestsIdAcceptPostClient = DefaultNotificationsApiV1NotificationsRequestsIdAcceptPostClient(configuration)

public class DefaultNotificationsApiV1NotificationsRequestsIdAcceptPostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : NotificationsApiV1NotificationsRequestsIdAcceptPostClient {
  override suspend fun postNotificationsRequestsByIdAccept(id: String): NotificationsApiV1NotificationsRequestsIdAcceptPostClient.PostNotificationsRequestsByIdAcceptResponse {
    try {
      val response = configuration.client.post("api/v1/notifications/requests/{id}/accept".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> NotificationsApiV1NotificationsRequestsIdAcceptPostClient.PostNotificationsRequestsByIdAcceptResponseSuccess(response.headers)
        401, 404, 429, 503 -> NotificationsApiV1NotificationsRequestsIdAcceptPostClient.PostNotificationsRequestsByIdAcceptResponseFailure401(response.body<Error>(), response.headers)
        410 -> NotificationsApiV1NotificationsRequestsIdAcceptPostClient.PostNotificationsRequestsByIdAcceptResponseFailure410(response.headers)
        422 -> NotificationsApiV1NotificationsRequestsIdAcceptPostClient.PostNotificationsRequestsByIdAcceptResponseFailure(response.body<ValidationError>(), response.headers)
        else -> NotificationsApiV1NotificationsRequestsIdAcceptPostClient.PostNotificationsRequestsByIdAcceptResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return NotificationsApiV1NotificationsRequestsIdAcceptPostClient.PostNotificationsRequestsByIdAcceptResponseUnknownFailure(500)
    }
  }
}
