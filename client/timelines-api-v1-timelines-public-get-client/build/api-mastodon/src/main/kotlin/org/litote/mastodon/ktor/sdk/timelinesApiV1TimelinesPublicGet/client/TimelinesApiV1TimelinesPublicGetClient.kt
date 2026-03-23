package org.litote.mastodon.ktor.sdk.timelinesApiV1TimelinesPublicGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import kotlin.Boolean
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.collections.List
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget83730355.model.Status

public class TimelinesApiV1TimelinesPublicGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * View public timeline
   */
  public suspend fun getTimelinePublic(
    limit: Long? = 20,
    local: Boolean? = false,
    maxId: String? = null,
    minId: String? = null,
    onlyMedia: Boolean? = false,
    remote: Boolean? = false,
    sinceId: String? = null,
  ): GetTimelinePublicResponse {
    try {
      val response = configuration.client.`get`("api/v1/timelines/public") {
        url {
          if (limit != null) {
            parameters.append("limit", limit.toString())
          }
          if (local != null) {
            parameters.append("local", local.toString())
          }
          if (maxId != null) {
            parameters.append("max_id", maxId)
          }
          if (minId != null) {
            parameters.append("min_id", minId)
          }
          if (onlyMedia != null) {
            parameters.append("only_media", onlyMedia.toString())
          }
          if (remote != null) {
            parameters.append("remote", remote.toString())
          }
          if (sinceId != null) {
            parameters.append("since_id", sinceId)
          }
        }
      }
      return when (response.status.value) {
        200 -> GetTimelinePublicResponseSuccess(response.body<List<Status>>())
        401, 404, 429, 503 -> GetTimelinePublicResponseFailure401(response.body<Error>())
        410 -> GetTimelinePublicResponseFailure410
        422 -> GetTimelinePublicResponseFailure(response.body<ValidationError>())
        else -> GetTimelinePublicResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return GetTimelinePublicResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class GetTimelinePublicResponse

  @Serializable
  public data class GetTimelinePublicResponseSuccess(
    public val body: List<Status>,
  ) : GetTimelinePublicResponse()

  @Serializable
  public data class GetTimelinePublicResponseFailure401(
    public val body: Error,
  ) : GetTimelinePublicResponse()

  @Serializable
  public object GetTimelinePublicResponseFailure410 : GetTimelinePublicResponse()

  @Serializable
  public data class GetTimelinePublicResponseFailure(
    public val body: ValidationError,
  ) : GetTimelinePublicResponse()

  @Serializable
  public data class GetTimelinePublicResponseUnknownFailure(
    public val statusCode: Int,
  ) : GetTimelinePublicResponse()
}
