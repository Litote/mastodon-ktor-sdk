package org.litote.mastodon.ktor.sdk.trendsApiV1TrendsLinksGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import kotlin.Int
import kotlin.Long
import kotlin.collections.List
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.model.TrendsLink
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError

public class TrendsApiV1TrendsLinksGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * View trending links
   */
  public suspend fun getTrendLinks(limit: Long? = 10, offset: Long? = null): GetTrendLinksResponse {
    try {
      val response = configuration.client.`get`("api/v1/trends/links") {
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
        200 -> GetTrendLinksResponseSuccess(response.body<List<TrendsLink>>())
        401, 404, 429, 503 -> GetTrendLinksResponseFailure401(response.body<Error>())
        410 -> GetTrendLinksResponseFailure410
        422 -> GetTrendLinksResponseFailure(response.body<ValidationError>())
        else -> GetTrendLinksResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return GetTrendLinksResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class GetTrendLinksResponse

  @Serializable
  public data class GetTrendLinksResponseSuccess(
    public val body: List<TrendsLink>,
  ) : GetTrendLinksResponse()

  @Serializable
  public data class GetTrendLinksResponseFailure401(
    public val body: Error,
  ) : GetTrendLinksResponse()

  @Serializable
  public object GetTrendLinksResponseFailure410 : GetTrendLinksResponse()

  @Serializable
  public data class GetTrendLinksResponseFailure(
    public val body: ValidationError,
  ) : GetTrendLinksResponse()

  @Serializable
  public data class GetTrendLinksResponseUnknownFailure(
    public val statusCode: Int,
  ) : GetTrendLinksResponse()
}
