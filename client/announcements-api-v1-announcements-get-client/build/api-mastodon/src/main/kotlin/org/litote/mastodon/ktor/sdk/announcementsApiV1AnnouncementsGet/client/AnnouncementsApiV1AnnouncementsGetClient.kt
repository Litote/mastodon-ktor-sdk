package org.litote.mastodon.ktor.sdk.announcementsApiV1AnnouncementsGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import kotlin.Int
import kotlin.collections.List
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.model.Announcement
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError

public class AnnouncementsApiV1AnnouncementsGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * View all announcements
   */
  public suspend fun getAnnouncements(): GetAnnouncementsResponse {
    try {
      val response = configuration.client.`get`("api/v1/announcements") {
      }
      return when (response.status.value) {
        200 -> GetAnnouncementsResponseSuccess(response.body<List<Announcement>>())
        401, 404, 429, 503 -> GetAnnouncementsResponseFailure401(response.body<Error>())
        410 -> GetAnnouncementsResponseFailure410
        422 -> GetAnnouncementsResponseFailure(response.body<ValidationError>())
        else -> GetAnnouncementsResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return GetAnnouncementsResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class GetAnnouncementsResponse

  @Serializable
  public data class GetAnnouncementsResponseSuccess(
    public val body: List<Announcement>,
  ) : GetAnnouncementsResponse()

  @Serializable
  public data class GetAnnouncementsResponseFailure401(
    public val body: Error,
  ) : GetAnnouncementsResponse()

  @Serializable
  public object GetAnnouncementsResponseFailure410 : GetAnnouncementsResponse()

  @Serializable
  public data class GetAnnouncementsResponseFailure(
    public val body: ValidationError,
  ) : GetAnnouncementsResponse()

  @Serializable
  public data class GetAnnouncementsResponseUnknownFailure(
    public val statusCode: Int,
  ) : GetAnnouncementsResponse()
}
