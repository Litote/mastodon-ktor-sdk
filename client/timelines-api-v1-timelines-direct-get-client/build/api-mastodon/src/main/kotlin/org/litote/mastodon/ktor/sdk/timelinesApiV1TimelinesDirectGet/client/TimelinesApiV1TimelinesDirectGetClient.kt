package org.litote.mastodon.ktor.sdk.timelinesApiV1TimelinesDirectGet.client

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

public class TimelinesApiV1TimelinesDirectGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * View direct timeline
   */
  public suspend fun getTimelineDirect(
    limit: Long? = 20,
    maxId: String? = null,
    minId: String? = null,
    sinceId: String? = null,
  ): GetTimelineDirectResponse {
    try {
      val response = configuration.client.`get`("api/v1/timelines/direct") {
        url {
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
        200 -> GetTimelineDirectResponseSuccess(response.body<List<Status>>())
        401, 404, 429, 503 -> GetTimelineDirectResponseFailure401(response.body<Error>())
        410 -> GetTimelineDirectResponseFailure410
        422 -> GetTimelineDirectResponseFailure(response.body<ValidationError>())
        else -> GetTimelineDirectResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return GetTimelineDirectResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class GetTimelineDirectResponse

  @Serializable
  public data class GetTimelineDirectResponseSuccess(
    public val body: List<Status>,
  ) : GetTimelineDirectResponse()

  @Serializable
  public data class GetTimelineDirectResponseFailure401(
    public val body: Error,
  ) : GetTimelineDirectResponse()

  @Serializable
  public object GetTimelineDirectResponseFailure410 : GetTimelineDirectResponse()

  @Serializable
  public data class GetTimelineDirectResponseFailure(
    public val body: ValidationError,
  ) : GetTimelineDirectResponse()

  @Serializable
  public data class GetTimelineDirectResponseUnknownFailure(
    public val statusCode: Int,
  ) : GetTimelineDirectResponse()
}
