package org.litote.mastodon.ktor.sdk.timelinesApiV1TimelinesListListIdGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import io.ktor.http.encodeURLPathPart
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

public class TimelinesApiV1TimelinesListListIdGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * View list timeline
   */
  public suspend fun getTimelinesListByListId(
    listId: String,
    limit: Long? = 20,
    maxId: String? = null,
    minId: String? = null,
    sinceId: String? = null,
  ): GetTimelinesListByListIdResponse {
    try {
      val response = configuration.client.`get`("api/v1/timelines/list/{list_id}".replace("/{list_id}", "/${listId.encodeURLPathPart()}")) {
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
        200 -> GetTimelinesListByListIdResponseSuccess(response.body<List<Status>>())
        401, 404, 429, 503 -> GetTimelinesListByListIdResponseFailure401(response.body<Error>())
        410 -> GetTimelinesListByListIdResponseFailure410
        422 -> GetTimelinesListByListIdResponseFailure(response.body<ValidationError>())
        else -> GetTimelinesListByListIdResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return GetTimelinesListByListIdResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class GetTimelinesListByListIdResponse

  @Serializable
  public data class GetTimelinesListByListIdResponseSuccess(
    public val body: List<Status>,
  ) : GetTimelinesListByListIdResponse()

  @Serializable
  public data class GetTimelinesListByListIdResponseFailure401(
    public val body: Error,
  ) : GetTimelinesListByListIdResponse()

  @Serializable
  public object GetTimelinesListByListIdResponseFailure410 : GetTimelinesListByListIdResponse()

  @Serializable
  public data class GetTimelinesListByListIdResponseFailure(
    public val body: ValidationError,
  ) : GetTimelinesListByListIdResponse()

  @Serializable
  public data class GetTimelinesListByListIdResponseUnknownFailure(
    public val statusCode: Int,
  ) : GetTimelinesListByListIdResponse()
}
