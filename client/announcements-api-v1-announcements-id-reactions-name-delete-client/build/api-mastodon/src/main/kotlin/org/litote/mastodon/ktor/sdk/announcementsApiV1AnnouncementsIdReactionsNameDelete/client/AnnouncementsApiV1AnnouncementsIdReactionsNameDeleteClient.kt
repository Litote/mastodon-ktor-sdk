package org.litote.mastodon.ktor.sdk.announcementsApiV1AnnouncementsIdReactionsNameDelete.client

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

public interface AnnouncementsApiV1AnnouncementsIdReactionsNameDeleteClient {
  /**
   * Remove a reaction from an announcement
   */
  public suspend fun deleteAnnouncementReaction(id: String, name: String): DeleteAnnouncementReactionResponse

  @Serializable
  public sealed class DeleteAnnouncementReactionResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class DeleteAnnouncementReactionResponseSuccess(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : DeleteAnnouncementReactionResponse() {
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
  public data class DeleteAnnouncementReactionResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : DeleteAnnouncementReactionResponse()

  @Serializable
  public data class DeleteAnnouncementReactionResponseFailure(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : DeleteAnnouncementReactionResponse()

  @Serializable
  public data class DeleteAnnouncementReactionResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : DeleteAnnouncementReactionResponse()
}

public fun AnnouncementsApiV1AnnouncementsIdReactionsNameDeleteClient(configuration: ClientConfiguration = defaultClientConfiguration): AnnouncementsApiV1AnnouncementsIdReactionsNameDeleteClient = DefaultAnnouncementsApiV1AnnouncementsIdReactionsNameDeleteClient(configuration)

public class DefaultAnnouncementsApiV1AnnouncementsIdReactionsNameDeleteClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : AnnouncementsApiV1AnnouncementsIdReactionsNameDeleteClient {
  override suspend fun deleteAnnouncementReaction(id: String, name: String): AnnouncementsApiV1AnnouncementsIdReactionsNameDeleteClient.DeleteAnnouncementReactionResponse {
    try {
      val response = configuration.client.delete("api/v1/announcements/{id}/reactions/{name}".replace("/{id}", "/${id.encodeURLPathPart()}").replace("/{name}", "/${name.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> AnnouncementsApiV1AnnouncementsIdReactionsNameDeleteClient.DeleteAnnouncementReactionResponseSuccess(response.headers)
        401, 404, 422, 429, 503 -> AnnouncementsApiV1AnnouncementsIdReactionsNameDeleteClient.DeleteAnnouncementReactionResponseFailure401(response.body<Error>(), response.headers)
        410 -> AnnouncementsApiV1AnnouncementsIdReactionsNameDeleteClient.DeleteAnnouncementReactionResponseFailure(response.headers)
        else -> AnnouncementsApiV1AnnouncementsIdReactionsNameDeleteClient.DeleteAnnouncementReactionResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return AnnouncementsApiV1AnnouncementsIdReactionsNameDeleteClient.DeleteAnnouncementReactionResponseUnknownFailure(500)
    }
  }
}
