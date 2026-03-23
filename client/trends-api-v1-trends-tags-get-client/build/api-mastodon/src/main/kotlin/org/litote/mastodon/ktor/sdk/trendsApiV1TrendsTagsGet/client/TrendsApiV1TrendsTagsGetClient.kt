package org.litote.mastodon.ktor.sdk.trendsApiV1TrendsTagsGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import kotlin.Int
import kotlin.Long
import kotlin.collections.List
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError
import org.litote.mastodon.ktor.sdk.sharedFeaturedtagsapiv1featuredtagssuggestionsget1c497d1f.model.Tag

public class TrendsApiV1TrendsTagsGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * View trending tags
   */
  public suspend fun getTrendTags(limit: Long? = 10, offset: Long? = null): GetTrendTagsResponse {
    try {
      val response = configuration.client.`get`("api/v1/trends/tags") {
        url {
          if (limit != null) {
            parameters.append("limit", limit.toString())
          }
          if (offset != null) {
            parameters.append("offset", offset.toString())
          }
        }
      }
      return when (response.status.value) {
        200 -> GetTrendTagsResponseSuccess(response.body<List<Tag>>())
        401, 404, 429, 503 -> GetTrendTagsResponseFailure401(response.body<Error>())
        410 -> GetTrendTagsResponseFailure410
        422 -> GetTrendTagsResponseFailure(response.body<ValidationError>())
        else -> GetTrendTagsResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return GetTrendTagsResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class GetTrendTagsResponse

  @Serializable
  public data class GetTrendTagsResponseSuccess(
    public val body: List<Tag>,
  ) : GetTrendTagsResponse()

  @Serializable
  public data class GetTrendTagsResponseFailure401(
    public val body: Error,
  ) : GetTrendTagsResponse()

  @Serializable
  public object GetTrendTagsResponseFailure410 : GetTrendTagsResponse()

  @Serializable
  public data class GetTrendTagsResponseFailure(
    public val body: ValidationError,
  ) : GetTrendTagsResponse()

  @Serializable
  public data class GetTrendTagsResponseUnknownFailure(
    public val statusCode: Int,
  ) : GetTrendTagsResponse()
}
