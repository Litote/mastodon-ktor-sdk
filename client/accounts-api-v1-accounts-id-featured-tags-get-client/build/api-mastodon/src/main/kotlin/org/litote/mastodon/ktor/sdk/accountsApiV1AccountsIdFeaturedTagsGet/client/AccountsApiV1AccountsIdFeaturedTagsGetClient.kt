package org.litote.mastodon.ktor.sdk.accountsApiV1AccountsIdFeaturedTagsGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import io.ktor.http.encodeURLPathPart
import kotlin.Int
import kotlin.String
import kotlin.collections.List
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidfeaturedtagsgetB2ed9160.model.FeaturedTag

public class AccountsApiV1AccountsIdFeaturedTagsGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Get account's featured tags
   */
  public suspend fun getAccountFeaturedTags(id: String): GetAccountFeaturedTagsResponse {
    try {
      val response = configuration.client.`get`("api/v1/accounts/{id}/featured_tags".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> GetAccountFeaturedTagsResponseSuccess(response.body<List<FeaturedTag>>())
        401, 404, 429, 503 -> GetAccountFeaturedTagsResponseFailure401(response.body<Error>())
        410 -> GetAccountFeaturedTagsResponseFailure410
        422 -> GetAccountFeaturedTagsResponseFailure(response.body<ValidationError>())
        else -> GetAccountFeaturedTagsResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return GetAccountFeaturedTagsResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class GetAccountFeaturedTagsResponse

  @Serializable
  public data class GetAccountFeaturedTagsResponseSuccess(
    public val body: List<FeaturedTag>,
  ) : GetAccountFeaturedTagsResponse()

  @Serializable
  public data class GetAccountFeaturedTagsResponseFailure401(
    public val body: Error,
  ) : GetAccountFeaturedTagsResponse()

  @Serializable
  public object GetAccountFeaturedTagsResponseFailure410 : GetAccountFeaturedTagsResponse()

  @Serializable
  public data class GetAccountFeaturedTagsResponseFailure(
    public val body: ValidationError,
  ) : GetAccountFeaturedTagsResponse()

  @Serializable
  public data class GetAccountFeaturedTagsResponseUnknownFailure(
    public val statusCode: Int,
  ) : GetAccountFeaturedTagsResponse()
}
