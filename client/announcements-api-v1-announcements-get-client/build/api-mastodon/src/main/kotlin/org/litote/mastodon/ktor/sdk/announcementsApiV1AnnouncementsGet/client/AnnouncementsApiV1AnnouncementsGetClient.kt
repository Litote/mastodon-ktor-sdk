package org.litote.mastodon.ktor.sdk.announcementsApiV1AnnouncementsGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import io.ktor.http.Headers
import kotlin.Int
import kotlin.String
import kotlin.collections.List
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.model.Announcement
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError

public interface AnnouncementsApiV1AnnouncementsGetClient {
  /**
   * View all announcements
   */
  public suspend fun getAnnouncements(): GetAnnouncementsResponse

  @Serializable
  public sealed class GetAnnouncementsResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class GetAnnouncementsResponseSuccess(
    public val body: List<Announcement>,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetAnnouncementsResponse() {
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
  public data class GetAnnouncementsResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetAnnouncementsResponse()

  @Serializable
  public data class GetAnnouncementsResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetAnnouncementsResponse()

  @Serializable
  public data class GetAnnouncementsResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetAnnouncementsResponse()

  @Serializable
  public data class GetAnnouncementsResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetAnnouncementsResponse()
}

public fun AnnouncementsApiV1AnnouncementsGetClient(configuration: ClientConfiguration = defaultClientConfiguration): AnnouncementsApiV1AnnouncementsGetClient = DefaultAnnouncementsApiV1AnnouncementsGetClient(configuration)

public class DefaultAnnouncementsApiV1AnnouncementsGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : AnnouncementsApiV1AnnouncementsGetClient {
  override suspend fun getAnnouncements(): AnnouncementsApiV1AnnouncementsGetClient.GetAnnouncementsResponse {
    try {
      val response = configuration.client.`get`("api/v1/announcements") {
      }
      return when (response.status.value) {
        200 -> AnnouncementsApiV1AnnouncementsGetClient.GetAnnouncementsResponseSuccess(response.body<List<Announcement>>(), response.headers)
        401, 404, 429, 503 -> AnnouncementsApiV1AnnouncementsGetClient.GetAnnouncementsResponseFailure401(response.body<Error>(), response.headers)
        410 -> AnnouncementsApiV1AnnouncementsGetClient.GetAnnouncementsResponseFailure410(response.headers)
        422 -> AnnouncementsApiV1AnnouncementsGetClient.GetAnnouncementsResponseFailure(response.body<ValidationError>(), response.headers)
        else -> AnnouncementsApiV1AnnouncementsGetClient.GetAnnouncementsResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return AnnouncementsApiV1AnnouncementsGetClient.GetAnnouncementsResponseUnknownFailure(500)
    }
  }
}
