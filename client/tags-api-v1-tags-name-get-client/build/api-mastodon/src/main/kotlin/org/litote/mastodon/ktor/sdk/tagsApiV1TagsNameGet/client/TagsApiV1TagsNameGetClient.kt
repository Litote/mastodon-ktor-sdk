package org.litote.mastodon.ktor.sdk.tagsApiV1TagsNameGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import io.ktor.http.encodeURLPathPart
import kotlin.Int
import kotlin.String
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError
import org.litote.mastodon.ktor.sdk.sharedFeaturedtagsapiv1featuredtagssuggestionsget1c497d1f.model.Tag

public class TagsApiV1TagsNameGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * View information about a single tag
   */
  public suspend fun getTagsByName(name: String): GetTagsByNameResponse {
    try {
      val response = configuration.client.`get`("api/v1/tags/{name}".replace("/{name}", "/${name.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> GetTagsByNameResponseSuccess(response.body<Tag>())
        401, 404, 429, 503 -> GetTagsByNameResponseFailure401(response.body<Error>())
        410 -> GetTagsByNameResponseFailure410
        422 -> GetTagsByNameResponseFailure(response.body<ValidationError>())
        else -> GetTagsByNameResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return GetTagsByNameResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class GetTagsByNameResponse

  @Serializable
  public data class GetTagsByNameResponseSuccess(
    public val body: Tag,
  ) : GetTagsByNameResponse()

  @Serializable
  public data class GetTagsByNameResponseFailure401(
    public val body: Error,
  ) : GetTagsByNameResponse()

  @Serializable
  public object GetTagsByNameResponseFailure410 : GetTagsByNameResponse()

  @Serializable
  public data class GetTagsByNameResponseFailure(
    public val body: ValidationError,
  ) : GetTagsByNameResponse()

  @Serializable
  public data class GetTagsByNameResponseUnknownFailure(
    public val statusCode: Int,
  ) : GetTagsByNameResponse()
}
