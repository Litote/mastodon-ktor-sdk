package org.litote.mastodon.ktor.sdk.timelinesApiV1TimelinesTagHashtagGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import io.ktor.http.encodeURLPathPart
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

public class TimelinesApiV1TimelinesTagHashtagGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * View hashtag timeline
   */
  public suspend fun getTimelinesTagByHashtag(
    hashtag: String,
    all: List<String>? = null,
    any: List<String>? = null,
    limit: Long? = 20,
    local: Boolean? = false,
    maxId: String? = null,
    minId: String? = null,
    none: List<String>? = null,
    onlyMedia: Boolean? = false,
    remote: Boolean? = false,
    sinceId: String? = null,
  ): GetTimelinesTagByHashtagResponse {
    try {
      val response = configuration.client.`get`("api/v1/timelines/tag/{hashtag}".replace("/{hashtag}", "/${hashtag.encodeURLPathPart()}")) {
        url {
          if (all != null) {
            parameters.append("all", all.joinToString(","))
          }
          if (any != null) {
            parameters.append("any", any.joinToString(","))
          }
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
          if (none != null) {
            parameters.append("none", none.joinToString(","))
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
        200 -> GetTimelinesTagByHashtagResponseSuccess(response.body<List<Status>>())
        401, 404, 429, 503 -> GetTimelinesTagByHashtagResponseFailure401(response.body<Error>())
        410 -> GetTimelinesTagByHashtagResponseFailure410
        422 -> GetTimelinesTagByHashtagResponseFailure(response.body<ValidationError>())
        else -> GetTimelinesTagByHashtagResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return GetTimelinesTagByHashtagResponseUnknownFailure(500)
    }
  }

  @Serializable
  public object All

  @Serializable
  public object Any

  @Serializable
  public object None

  @Serializable
  public sealed class GetTimelinesTagByHashtagResponse

  @Serializable
  public data class GetTimelinesTagByHashtagResponseSuccess(
    public val body: List<Status>,
  ) : GetTimelinesTagByHashtagResponse()

  @Serializable
  public data class GetTimelinesTagByHashtagResponseFailure401(
    public val body: Error,
  ) : GetTimelinesTagByHashtagResponse()

  @Serializable
  public object GetTimelinesTagByHashtagResponseFailure410 : GetTimelinesTagByHashtagResponse()

  @Serializable
  public data class GetTimelinesTagByHashtagResponseFailure(
    public val body: ValidationError,
  ) : GetTimelinesTagByHashtagResponse()

  @Serializable
  public data class GetTimelinesTagByHashtagResponseUnknownFailure(
    public val statusCode: Int,
  ) : GetTimelinesTagByHashtagResponse()
}
