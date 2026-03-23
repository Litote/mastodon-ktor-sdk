package org.litote.mastodon.ktor.sdk.featuredTagsApiV1FeaturedTagsIdDelete.client

import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.http.encodeURLPathPart
import kotlin.Int
import kotlin.String
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError

public class FeaturedTagsApiV1FeaturedTagsIdDeleteClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Unfeature a tag
   */
  public suspend fun deleteFeaturedTag(id: String): DeleteFeaturedTagResponse {
    try {
      val response = configuration.client.delete("api/v1/featured_tags/{id}".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> DeleteFeaturedTagResponseSuccess
        401, 404, 429, 503 -> DeleteFeaturedTagResponseFailure401(response.body<Error>())
        410 -> DeleteFeaturedTagResponseFailure410
        422 -> DeleteFeaturedTagResponseFailure(response.body<ValidationError>())
        else -> DeleteFeaturedTagResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return DeleteFeaturedTagResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class DeleteFeaturedTagResponse

  @Serializable
  public object DeleteFeaturedTagResponseSuccess : DeleteFeaturedTagResponse()

  @Serializable
  public data class DeleteFeaturedTagResponseFailure401(
    public val body: Error,
  ) : DeleteFeaturedTagResponse()

  @Serializable
  public object DeleteFeaturedTagResponseFailure410 : DeleteFeaturedTagResponse()

  @Serializable
  public data class DeleteFeaturedTagResponseFailure(
    public val body: ValidationError,
  ) : DeleteFeaturedTagResponse()

  @Serializable
  public data class DeleteFeaturedTagResponseUnknownFailure(
    public val statusCode: Int,
  ) : DeleteFeaturedTagResponse()
}
