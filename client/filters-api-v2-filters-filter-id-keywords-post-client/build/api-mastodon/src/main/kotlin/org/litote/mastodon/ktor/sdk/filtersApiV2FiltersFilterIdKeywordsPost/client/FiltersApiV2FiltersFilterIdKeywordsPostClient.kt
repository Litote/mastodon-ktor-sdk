package org.litote.mastodon.ktor.sdk.filtersApiV2FiltersFilterIdKeywordsPost.client

import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.encodeURLPathPart
import kotlin.Boolean
import kotlin.Int
import kotlin.String
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesgetB0793237.model.FilterKeyword

public class FiltersApiV2FiltersFilterIdKeywordsPostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Add a keyword to a filter
   */
  public suspend fun postFilterKeywordsV2(request: PostFilterKeywordsV2Request, filterId: String): PostFilterKeywordsV2Response {
    try {
      val response = configuration.client.post("api/v2/filters/{filter_id}/keywords".replace("/{filter_id}", "/${filterId.encodeURLPathPart()}")) {
        setBody(request)
        contentType(ContentType.Application.Json)
      }
      return when (response.status.value) {
        200 -> PostFilterKeywordsV2ResponseSuccess(response.body<FilterKeyword>())
        401, 404, 422, 429, 503 -> PostFilterKeywordsV2ResponseFailure401(response.body<Error>())
        410 -> PostFilterKeywordsV2ResponseFailure
        else -> PostFilterKeywordsV2ResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return PostFilterKeywordsV2ResponseUnknownFailure(500)
    }
  }

  @Serializable
  public data class PostFilterKeywordsV2Request(
    public val keyword: String,
    @SerialName("whole_word")
    public val wholeWord: Boolean? = null,
  )

  @Serializable
  public sealed class PostFilterKeywordsV2Response

  @Serializable
  public data class PostFilterKeywordsV2ResponseSuccess(
    public val body: FilterKeyword,
  ) : PostFilterKeywordsV2Response()

  @Serializable
  public data class PostFilterKeywordsV2ResponseFailure401(
    public val body: Error,
  ) : PostFilterKeywordsV2Response()

  @Serializable
  public object PostFilterKeywordsV2ResponseFailure : PostFilterKeywordsV2Response()

  @Serializable
  public data class PostFilterKeywordsV2ResponseUnknownFailure(
    public val statusCode: Int,
  ) : PostFilterKeywordsV2Response()
}
