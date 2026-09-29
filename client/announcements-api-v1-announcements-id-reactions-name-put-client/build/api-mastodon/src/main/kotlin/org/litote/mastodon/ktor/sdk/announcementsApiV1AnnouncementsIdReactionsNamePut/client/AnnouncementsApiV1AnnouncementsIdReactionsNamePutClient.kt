package org.litote.mastodon.ktor.sdk.announcementsApiV1AnnouncementsIdReactionsNamePut.client

import io.ktor.client.call.body
import io.ktor.client.request.put
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

public interface AnnouncementsApiV1AnnouncementsIdReactionsNamePutClient {
  /**
   * Add a reaction to an announcement
   */
  public suspend fun updateAnnouncementReaction(id: String, name: String): UpdateAnnouncementReactionResponse

  @Serializable
  public sealed class UpdateAnnouncementReactionResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class UpdateAnnouncementReactionResponseSuccess(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : UpdateAnnouncementReactionResponse() {
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
  public data class UpdateAnnouncementReactionResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : UpdateAnnouncementReactionResponse()

  @Serializable
  public data class UpdateAnnouncementReactionResponseFailure(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : UpdateAnnouncementReactionResponse()

  @Serializable
  public data class UpdateAnnouncementReactionResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : UpdateAnnouncementReactionResponse()
}

public fun AnnouncementsApiV1AnnouncementsIdReactionsNamePutClient(configuration: ClientConfiguration = defaultClientConfiguration): AnnouncementsApiV1AnnouncementsIdReactionsNamePutClient = DefaultAnnouncementsApiV1AnnouncementsIdReactionsNamePutClient(configuration)

public class DefaultAnnouncementsApiV1AnnouncementsIdReactionsNamePutClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : AnnouncementsApiV1AnnouncementsIdReactionsNamePutClient {
  override suspend fun updateAnnouncementReaction(id: String, name: String): AnnouncementsApiV1AnnouncementsIdReactionsNamePutClient.UpdateAnnouncementReactionResponse {
    try {
      val response = configuration.client.put("api/v1/announcements/{id}/reactions/{name}".replace("/{id}", "/${id.encodeURLPathPart()}").replace("/{name}", "/${name.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> AnnouncementsApiV1AnnouncementsIdReactionsNamePutClient.UpdateAnnouncementReactionResponseSuccess(response.headers)
        401, 404, 422, 429, 503 -> AnnouncementsApiV1AnnouncementsIdReactionsNamePutClient.UpdateAnnouncementReactionResponseFailure401(response.body<Error>(), response.headers)
        410 -> AnnouncementsApiV1AnnouncementsIdReactionsNamePutClient.UpdateAnnouncementReactionResponseFailure(response.headers)
        else -> AnnouncementsApiV1AnnouncementsIdReactionsNamePutClient.UpdateAnnouncementReactionResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return AnnouncementsApiV1AnnouncementsIdReactionsNamePutClient.UpdateAnnouncementReactionResponseUnknownFailure(500)
    }
  }
}
