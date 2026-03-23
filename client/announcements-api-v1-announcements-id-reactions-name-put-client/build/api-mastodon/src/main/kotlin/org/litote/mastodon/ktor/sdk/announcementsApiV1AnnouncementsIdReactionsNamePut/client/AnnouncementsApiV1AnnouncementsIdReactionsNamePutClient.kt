package org.litote.mastodon.ktor.sdk.announcementsApiV1AnnouncementsIdReactionsNamePut.client

import io.ktor.client.call.body
import io.ktor.client.request.put
import io.ktor.http.encodeURLPathPart
import kotlin.Int
import kotlin.String
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error

public class AnnouncementsApiV1AnnouncementsIdReactionsNamePutClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Add a reaction to an announcement
   */
  public suspend fun updateAnnouncementReaction(id: String, name: String): UpdateAnnouncementReactionResponse {
    try {
      val response = configuration.client.put("api/v1/announcements/{id}/reactions/{name}".replace("/{id}", "/${id.encodeURLPathPart()}").replace("/{name}", "/${name.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> UpdateAnnouncementReactionResponseSuccess
        401, 404, 422, 429, 503 -> UpdateAnnouncementReactionResponseFailure401(response.body<Error>())
        410 -> UpdateAnnouncementReactionResponseFailure
        else -> UpdateAnnouncementReactionResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return UpdateAnnouncementReactionResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class UpdateAnnouncementReactionResponse

  @Serializable
  public object UpdateAnnouncementReactionResponseSuccess : UpdateAnnouncementReactionResponse()

  @Serializable
  public data class UpdateAnnouncementReactionResponseFailure401(
    public val body: Error,
  ) : UpdateAnnouncementReactionResponse()

  @Serializable
  public object UpdateAnnouncementReactionResponseFailure : UpdateAnnouncementReactionResponse()

  @Serializable
  public data class UpdateAnnouncementReactionResponseUnknownFailure(
    public val statusCode: Int,
  ) : UpdateAnnouncementReactionResponse()
}
