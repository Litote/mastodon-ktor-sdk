package org.litote.mastodon.ktor.sdk.statusesApiV1StatusesIdDelete.client

import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.http.Headers
import io.ktor.http.encodeURLPathPart
import kotlin.Boolean
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

public interface StatusesApiV1StatusesIdDeleteClient {
  /**
   * Delete a status
   */
  public suspend fun deleteStatus(id: String, deleteMedia: Boolean? = null): DeleteStatusResponse

  @Serializable
  public sealed class DeleteStatusResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class DeleteStatusResponseSuccess(
    public val body: Status,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : DeleteStatusResponse() {
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
  public data class DeleteStatusResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : DeleteStatusResponse()

  @Serializable
  public data class DeleteStatusResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : DeleteStatusResponse()

  @Serializable
  public data class DeleteStatusResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : DeleteStatusResponse()

  @Serializable
  public data class DeleteStatusResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : DeleteStatusResponse()
}

public fun StatusesApiV1StatusesIdDeleteClient(configuration: ClientConfiguration = defaultClientConfiguration): StatusesApiV1StatusesIdDeleteClient = DefaultStatusesApiV1StatusesIdDeleteClient(configuration)

public class DefaultStatusesApiV1StatusesIdDeleteClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : StatusesApiV1StatusesIdDeleteClient {
  override suspend fun deleteStatus(id: String, deleteMedia: Boolean?): StatusesApiV1StatusesIdDeleteClient.DeleteStatusResponse {
    try {
      val response = configuration.client.delete("api/v1/statuses/{id}".replace("/{id}", "/${id.encodeURLPathPart()}")) {
        url {
          if (deleteMedia != null) {
            parameters.append("delete_media", deleteMedia.toString())
          }
        }
      }
      return when (response.status.value) {
        200 -> StatusesApiV1StatusesIdDeleteClient.DeleteStatusResponseSuccess(response.body<Status>(), response.headers)
        401, 404, 429, 503 -> StatusesApiV1StatusesIdDeleteClient.DeleteStatusResponseFailure401(response.body<Error>(), response.headers)
        410 -> StatusesApiV1StatusesIdDeleteClient.DeleteStatusResponseFailure410(response.headers)
        422 -> StatusesApiV1StatusesIdDeleteClient.DeleteStatusResponseFailure(response.body<ValidationError>(), response.headers)
        else -> StatusesApiV1StatusesIdDeleteClient.DeleteStatusResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return StatusesApiV1StatusesIdDeleteClient.DeleteStatusResponseUnknownFailure(500)
    }
  }
}
