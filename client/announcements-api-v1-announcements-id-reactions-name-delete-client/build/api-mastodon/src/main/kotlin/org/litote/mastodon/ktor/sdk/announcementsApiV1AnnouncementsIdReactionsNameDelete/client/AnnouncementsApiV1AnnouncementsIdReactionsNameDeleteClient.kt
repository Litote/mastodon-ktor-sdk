package org.litote.mastodon.ktor.sdk.announcementsApiV1AnnouncementsIdReactionsNameDelete.client

import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.http.encodeURLPathPart
import kotlin.Int
import kotlin.String
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error

public class AnnouncementsApiV1AnnouncementsIdReactionsNameDeleteClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Remove a reaction from an announcement
   */
  public suspend fun deleteAnnouncementReaction(id: String, name: String): DeleteAnnouncementReactionResponse {
    try {
      val response = configuration.client.delete("api/v1/announcements/{id}/reactions/{name}".replace("/{id}", "/${id.encodeURLPathPart()}").replace("/{name}", "/${name.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> DeleteAnnouncementReactionResponseSuccess
        401, 404, 422, 429, 503 -> DeleteAnnouncementReactionResponseFailure401(response.body<Error>())
        410 -> DeleteAnnouncementReactionResponseFailure
        else -> DeleteAnnouncementReactionResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return DeleteAnnouncementReactionResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class DeleteAnnouncementReactionResponse

  @Serializable
  public object DeleteAnnouncementReactionResponseSuccess : DeleteAnnouncementReactionResponse()

  @Serializable
  public data class DeleteAnnouncementReactionResponseFailure401(
    public val body: Error,
  ) : DeleteAnnouncementReactionResponse()

  @Serializable
  public object DeleteAnnouncementReactionResponseFailure : DeleteAnnouncementReactionResponse()

  @Serializable
  public data class DeleteAnnouncementReactionResponseUnknownFailure(
    public val statusCode: Int,
  ) : DeleteAnnouncementReactionResponse()
}
