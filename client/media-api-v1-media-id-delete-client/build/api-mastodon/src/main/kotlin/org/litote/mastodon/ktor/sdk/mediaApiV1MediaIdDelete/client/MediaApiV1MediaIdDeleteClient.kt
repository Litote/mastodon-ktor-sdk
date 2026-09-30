package org.litote.mastodon.ktor.sdk.mediaApiV1MediaIdDelete.client

import io.ktor.client.call.body
import io.ktor.client.request.delete
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

public interface MediaApiV1MediaIdDeleteClient {
  /**
   * Delete media attachment
   */
  public suspend fun deleteMedia(id: String): DeleteMediaResponse

  @Serializable
  public sealed class DeleteMediaResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class DeleteMediaResponseSuccess(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : DeleteMediaResponse() {
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
  public data class DeleteMediaResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : DeleteMediaResponse()

  @Serializable
  public data class DeleteMediaResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : DeleteMediaResponse()

  @Serializable
  public data class DeleteMediaResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : DeleteMediaResponse()

  @Serializable
  public data class DeleteMediaResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : DeleteMediaResponse()
}

public fun MediaApiV1MediaIdDeleteClient(configuration: ClientConfiguration = defaultClientConfiguration): MediaApiV1MediaIdDeleteClient = DefaultMediaApiV1MediaIdDeleteClient(configuration)

public class DefaultMediaApiV1MediaIdDeleteClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : MediaApiV1MediaIdDeleteClient {
  override suspend fun deleteMedia(id: String): MediaApiV1MediaIdDeleteClient.DeleteMediaResponse {
    try {
      val response = configuration.client.delete("api/v1/media/{id}".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> MediaApiV1MediaIdDeleteClient.DeleteMediaResponseSuccess(response.headers)
        401, 404, 429, 503 -> MediaApiV1MediaIdDeleteClient.DeleteMediaResponseFailure401(response.body<Error>(), response.headers)
        410 -> MediaApiV1MediaIdDeleteClient.DeleteMediaResponseFailure410(response.headers)
        422 -> MediaApiV1MediaIdDeleteClient.DeleteMediaResponseFailure(response.body<ValidationError>(), response.headers)
        else -> MediaApiV1MediaIdDeleteClient.DeleteMediaResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return MediaApiV1MediaIdDeleteClient.DeleteMediaResponseUnknownFailure(500)
    }
  }
}
