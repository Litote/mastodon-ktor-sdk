package org.litote.mastodon.ktor.sdk.announcementsApiV1AnnouncementsIdDismissPost.client

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

public interface AnnouncementsApiV1AnnouncementsIdDismissPostClient {
  /**
   * Dismiss an announcement
   */
  public suspend fun postAnnouncementDismiss(id: String): PostAnnouncementDismissResponse

  @Serializable
  public sealed class PostAnnouncementDismissResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class PostAnnouncementDismissResponseSuccess(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostAnnouncementDismissResponse() {
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
  public data class PostAnnouncementDismissResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostAnnouncementDismissResponse()

  @Serializable
  public data class PostAnnouncementDismissResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostAnnouncementDismissResponse()

  @Serializable
  public data class PostAnnouncementDismissResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostAnnouncementDismissResponse()

  @Serializable
  public data class PostAnnouncementDismissResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostAnnouncementDismissResponse()
}

public fun AnnouncementsApiV1AnnouncementsIdDismissPostClient(configuration: ClientConfiguration = defaultClientConfiguration): AnnouncementsApiV1AnnouncementsIdDismissPostClient = DefaultAnnouncementsApiV1AnnouncementsIdDismissPostClient(configuration)

public class DefaultAnnouncementsApiV1AnnouncementsIdDismissPostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : AnnouncementsApiV1AnnouncementsIdDismissPostClient {
  override suspend fun postAnnouncementDismiss(id: String): AnnouncementsApiV1AnnouncementsIdDismissPostClient.PostAnnouncementDismissResponse {
    try {
      val response = configuration.client.post("api/v1/announcements/{id}/dismiss".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> AnnouncementsApiV1AnnouncementsIdDismissPostClient.PostAnnouncementDismissResponseSuccess(response.headers)
        401, 404, 429, 503 -> AnnouncementsApiV1AnnouncementsIdDismissPostClient.PostAnnouncementDismissResponseFailure401(response.body<Error>(), response.headers)
        410 -> AnnouncementsApiV1AnnouncementsIdDismissPostClient.PostAnnouncementDismissResponseFailure410(response.headers)
        422 -> AnnouncementsApiV1AnnouncementsIdDismissPostClient.PostAnnouncementDismissResponseFailure(response.body<ValidationError>(), response.headers)
        else -> AnnouncementsApiV1AnnouncementsIdDismissPostClient.PostAnnouncementDismissResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return AnnouncementsApiV1AnnouncementsIdDismissPostClient.PostAnnouncementDismissResponseUnknownFailure(500)
    }
  }
}
