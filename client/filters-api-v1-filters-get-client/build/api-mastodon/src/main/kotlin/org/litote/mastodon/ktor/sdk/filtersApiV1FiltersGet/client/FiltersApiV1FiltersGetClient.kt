package org.litote.mastodon.ktor.sdk.filtersApiV1FiltersGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import kotlin.Int
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError
import org.litote.mastodon.ktor.sdk.sharedFiltersapiv1filtersget35506993.model.V1Filter

public class FiltersApiV1FiltersGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * View your filters
   */
  public suspend fun getFilters(): GetFiltersResponse {
    try {
      val response = configuration.client.`get`("api/v1/filters") {
      }
      return when (response.status.value) {
        200 -> GetFiltersResponseSuccess(response.body<V1Filter>())
        401, 404, 429, 503 -> GetFiltersResponseFailure401(response.body<Error>())
        410 -> GetFiltersResponseFailure410
        422 -> GetFiltersResponseFailure(response.body<ValidationError>())
        else -> GetFiltersResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return GetFiltersResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class GetFiltersResponse

  @Serializable
  public data class GetFiltersResponseSuccess(
    public val body: V1Filter,
  ) : GetFiltersResponse()

  @Serializable
  public data class GetFiltersResponseFailure401(
    public val body: Error,
  ) : GetFiltersResponse()

  @Serializable
  public object GetFiltersResponseFailure410 : GetFiltersResponse()

  @Serializable
  public data class GetFiltersResponseFailure(
    public val body: ValidationError,
  ) : GetFiltersResponse()

  @Serializable
  public data class GetFiltersResponseUnknownFailure(
    public val statusCode: Int,
  ) : GetFiltersResponse()
}
