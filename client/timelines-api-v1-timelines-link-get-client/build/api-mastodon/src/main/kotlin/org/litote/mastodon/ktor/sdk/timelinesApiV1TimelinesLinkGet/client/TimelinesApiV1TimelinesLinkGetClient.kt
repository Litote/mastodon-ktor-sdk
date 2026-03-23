package org.litote.mastodon.ktor.sdk.timelinesApiV1TimelinesLinkGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
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

public class TimelinesApiV1TimelinesLinkGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * View link timeline
   */
  public suspend fun getTimelineLink(
    url: String,
    limit: Long? = 20,
    maxId: String? = null,
    minId: String? = null,
    sinceId: String? = null,
  ): GetTimelineLinkResponse {
    try {
      val response = configuration.client.`get`("api/v1/timelines/link") {
        url {
          parameters.append("url", url)
          if (limit != null) {
            parameters.append("limit", limit.toString())
          }
          if (maxId != null) {
            parameters.append("max_id", maxId)
          }
          if (minId != null) {
            parameters.append("min_id", minId)
          }
          if (sinceId != null) {
            parameters.append("since_id", sinceId)
          }
        }
      }
      return when (response.status.value) {
        200 -> GetTimelineLinkResponseSuccess(response.body<List<Status>>())
        401, 404, 429, 503 -> GetTimelineLinkResponseFailure401(response.body<Error>())
        410 -> GetTimelineLinkResponseFailure410
        422 -> GetTimelineLinkResponseFailure(response.body<ValidationError>())
        else -> GetTimelineLinkResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return GetTimelineLinkResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class GetTimelineLinkResponse

  @Serializable
  public data class GetTimelineLinkResponseSuccess(
    public val body: List<Status>,
  ) : GetTimelineLinkResponse()

  @Serializable
  public data class GetTimelineLinkResponseFailure401(
    public val body: Error,
  ) : GetTimelineLinkResponse()

  @Serializable
  public object GetTimelineLinkResponseFailure410 : GetTimelineLinkResponse()

  @Serializable
  public data class GetTimelineLinkResponseFailure(
    public val body: ValidationError,
  ) : GetTimelineLinkResponse()

  @Serializable
  public data class GetTimelineLinkResponseUnknownFailure(
    public val statusCode: Int,
  ) : GetTimelineLinkResponse()
}
