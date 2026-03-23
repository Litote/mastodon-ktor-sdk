package org.litote.mastodon.ktor.sdk.timelinesApiV1TimelinesHomeGet.client

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

public class TimelinesApiV1TimelinesHomeGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * View home timeline
   */
  public suspend fun getTimelineHome(
    limit: Long? = 20,
    maxId: String? = null,
    minId: String? = null,
    sinceId: String? = null,
  ): GetTimelineHomeResponse {
    try {
      val response = configuration.client.`get`("api/v1/timelines/home") {
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
        200 -> GetTimelineHomeResponseSuccess200(response.body<List<Status>>())
        206 -> GetTimelineHomeResponseSuccess
        401, 404, 429, 503 -> GetTimelineHomeResponseFailure401(response.body<Error>())
        410 -> GetTimelineHomeResponseFailure410
        422 -> GetTimelineHomeResponseFailure(response.body<ValidationError>())
        else -> GetTimelineHomeResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return GetTimelineHomeResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class GetTimelineHomeResponse

  @Serializable
  public data class GetTimelineHomeResponseSuccess200(
    public val body: List<Status>,
  ) : GetTimelineHomeResponse()

  @Serializable
  public object GetTimelineHomeResponseSuccess : GetTimelineHomeResponse()

  @Serializable
  public data class GetTimelineHomeResponseFailure401(
    public val body: Error,
  ) : GetTimelineHomeResponse()

  @Serializable
  public object GetTimelineHomeResponseFailure410 : GetTimelineHomeResponse()

  @Serializable
  public data class GetTimelineHomeResponseFailure(
    public val body: ValidationError,
  ) : GetTimelineHomeResponse()

  @Serializable
  public data class GetTimelineHomeResponseUnknownFailure(
    public val statusCode: Int,
  ) : GetTimelineHomeResponse()
}
