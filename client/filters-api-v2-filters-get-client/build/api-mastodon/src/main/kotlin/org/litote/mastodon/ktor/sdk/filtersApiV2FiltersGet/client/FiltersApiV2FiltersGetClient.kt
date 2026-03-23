package org.litote.mastodon.ktor.sdk.filtersApiV2FiltersGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import kotlin.Int
import kotlin.collections.List
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget04cf5721.model.Filter

public class FiltersApiV2FiltersGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * View all filters
   */
  public suspend fun getFiltersV2(): GetFiltersV2Response {
    try {
      val response = configuration.client.`get`("api/v2/filters") {
      }
      return when (response.status.value) {
        200 -> GetFiltersV2ResponseSuccess(response.body<List<Filter>>())
        401, 404, 429, 503 -> GetFiltersV2ResponseFailure401(response.body<Error>())
        410 -> GetFiltersV2ResponseFailure410
        422 -> GetFiltersV2ResponseFailure(response.body<ValidationError>())
        else -> GetFiltersV2ResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return GetFiltersV2ResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class GetFiltersV2Response

  @Serializable
  public data class GetFiltersV2ResponseSuccess(
    public val body: List<Filter>,
  ) : GetFiltersV2Response()

  @Serializable
  public data class GetFiltersV2ResponseFailure401(
    public val body: Error,
  ) : GetFiltersV2Response()

  @Serializable
  public object GetFiltersV2ResponseFailure410 : GetFiltersV2Response()

  @Serializable
  public data class GetFiltersV2ResponseFailure(
    public val body: ValidationError,
  ) : GetFiltersV2Response()

  @Serializable
  public data class GetFiltersV2ResponseUnknownFailure(
    public val statusCode: Int,
  ) : GetFiltersV2Response()
}
