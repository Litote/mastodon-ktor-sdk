package org.litote.mastodon.ktor.sdk.notificationsApiV1NotificationsRequestsAcceptPost.client

import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.http.Headers
import kotlin.Int
import kotlin.String
import kotlin.Unit
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

public interface NotificationsApiV1NotificationsRequestsAcceptPostClient {
  /**
   * Accept multiple notification requests
   */
  public suspend fun createNotificationsRequestsAccept(): CreateNotificationsRequestsAcceptResponse

  @Serializable
  public sealed class CreateNotificationsRequestsAcceptResponse : OperationResponse<Unit> {
    abstract override val headers: Headers
  }

  @Serializable
  public data class CreateNotificationsRequestsAcceptResponseSuccess(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : CreateNotificationsRequestsAcceptResponse(),
      OperationResponseSuccess<Unit> {
    override val body: Unit
      get() = Unit

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
  public data class CreateNotificationsRequestsAcceptResponseFailure401(
    override val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : CreateNotificationsRequestsAcceptResponse(),
      OperationResponseFailure<Error>

  @Serializable
  public data class CreateNotificationsRequestsAcceptResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : CreateNotificationsRequestsAcceptResponse(),
      OperationResponseFailure<Unit> {
    override val body: Unit
      get() = Unit
  }

  @Serializable
  public data class CreateNotificationsRequestsAcceptResponseFailure(
    override val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : CreateNotificationsRequestsAcceptResponse(),
      OperationResponseFailure<ValidationError>

  @Serializable
  public data class CreateNotificationsRequestsAcceptResponseUnknownFailure(
    override val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : CreateNotificationsRequestsAcceptResponse(),
      OperationResponseUnknownFailure
}

public fun NotificationsApiV1NotificationsRequestsAcceptPostClient(configuration: ClientConfiguration = defaultClientConfiguration): NotificationsApiV1NotificationsRequestsAcceptPostClient = DefaultNotificationsApiV1NotificationsRequestsAcceptPostClient(configuration)

public class DefaultNotificationsApiV1NotificationsRequestsAcceptPostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : NotificationsApiV1NotificationsRequestsAcceptPostClient {
  override suspend fun createNotificationsRequestsAccept(): NotificationsApiV1NotificationsRequestsAcceptPostClient.CreateNotificationsRequestsAcceptResponse {
    try {
      val response = configuration.client.post("api/v1/notifications/requests/accept") {
      }
      return when (response.status.value) {
        200 -> NotificationsApiV1NotificationsRequestsAcceptPostClient.CreateNotificationsRequestsAcceptResponseSuccess(response.headers)
        401, 404, 429, 503 -> NotificationsApiV1NotificationsRequestsAcceptPostClient.CreateNotificationsRequestsAcceptResponseFailure401(response.body<Error>(), response.headers)
        410 -> NotificationsApiV1NotificationsRequestsAcceptPostClient.CreateNotificationsRequestsAcceptResponseFailure410(response.headers)
        422 -> NotificationsApiV1NotificationsRequestsAcceptPostClient.CreateNotificationsRequestsAcceptResponseFailure(response.body<ValidationError>(), response.headers)
        else -> NotificationsApiV1NotificationsRequestsAcceptPostClient.CreateNotificationsRequestsAcceptResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return NotificationsApiV1NotificationsRequestsAcceptPostClient.CreateNotificationsRequestsAcceptResponseUnknownFailure(500)
    }
  }
}
