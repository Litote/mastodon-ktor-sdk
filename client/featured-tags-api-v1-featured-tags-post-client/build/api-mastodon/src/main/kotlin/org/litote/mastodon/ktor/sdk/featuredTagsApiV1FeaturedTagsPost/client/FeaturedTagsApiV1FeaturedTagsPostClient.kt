package org.litote.mastodon.ktor.sdk.featuredTagsApiV1FeaturedTagsPost.client

import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlin.Int
import kotlin.String
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidfeaturedtagsgetB2ed9160.model.FeaturedTag

public class FeaturedTagsApiV1FeaturedTagsPostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Feature a tag
   */
  public suspend fun createFeaturedTag(request: CreateFeaturedTagRequest): CreateFeaturedTagResponse {
    try {
      val response = configuration.client.post("api/v1/featured_tags") {
        setBody(request)
        contentType(ContentType.Application.Json)
      }
      return when (response.status.value) {
        200 -> CreateFeaturedTagResponseSuccess(response.body<FeaturedTag>())
        401, 404, 422, 429, 503 -> CreateFeaturedTagResponseFailure401(response.body<Error>())
        410 -> CreateFeaturedTagResponseFailure
        else -> CreateFeaturedTagResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return CreateFeaturedTagResponseUnknownFailure(500)
    }
  }

  @Serializable
  public data class CreateFeaturedTagRequest(
    public val name: String,
  )

  @Serializable
  public sealed class CreateFeaturedTagResponse

  @Serializable
  public data class CreateFeaturedTagResponseSuccess(
    public val body: FeaturedTag,
  ) : CreateFeaturedTagResponse()

  @Serializable
  public data class CreateFeaturedTagResponseFailure401(
    public val body: Error,
  ) : CreateFeaturedTagResponse()

  @Serializable
  public object CreateFeaturedTagResponseFailure : CreateFeaturedTagResponse()

  @Serializable
  public data class CreateFeaturedTagResponseUnknownFailure(
    public val statusCode: Int,
  ) : CreateFeaturedTagResponse()
}
