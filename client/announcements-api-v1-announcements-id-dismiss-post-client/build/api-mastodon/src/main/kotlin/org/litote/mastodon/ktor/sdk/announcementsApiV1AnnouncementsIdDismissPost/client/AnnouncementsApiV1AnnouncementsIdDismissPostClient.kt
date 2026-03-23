package org.litote.mastodon.ktor.sdk.announcementsApiV1AnnouncementsIdDismissPost.client

import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.http.encodeURLPathPart
import kotlin.Int
import kotlin.String
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError

public class AnnouncementsApiV1AnnouncementsIdDismissPostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Dismiss an announcement
   */
  public suspend fun postAnnouncementDismiss(id: String): PostAnnouncementDismissResponse {
    try {
      val response = configuration.client.post("api/v1/announcements/{id}/dismiss".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> PostAnnouncementDismissResponseSuccess
        401, 404, 429, 503 -> PostAnnouncementDismissResponseFailure401(response.body<Error>())
        410 -> PostAnnouncementDismissResponseFailure410
        422 -> PostAnnouncementDismissResponseFailure(response.body<ValidationError>())
        else -> PostAnnouncementDismissResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return PostAnnouncementDismissResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class PostAnnouncementDismissResponse

  @Serializable
  public object PostAnnouncementDismissResponseSuccess : PostAnnouncementDismissResponse()

  @Serializable
  public data class PostAnnouncementDismissResponseFailure401(
    public val body: Error,
  ) : PostAnnouncementDismissResponse()

  @Serializable
  public object PostAnnouncementDismissResponseFailure410 : PostAnnouncementDismissResponse()

  @Serializable
  public data class PostAnnouncementDismissResponseFailure(
    public val body: ValidationError,
  ) : PostAnnouncementDismissResponse()

  @Serializable
  public data class PostAnnouncementDismissResponseUnknownFailure(
    public val statusCode: Int,
  ) : PostAnnouncementDismissResponse()
}
