package org.litote.mastodon.ktor.sdk.filtersApiV1FiltersIdGet.client

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
import org.litote.mastodon.ktor.sdk.sharedFiltersapiv1filtersget35506993.model.V1Filter

public class FiltersApiV1FiltersIdGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * View a single filter
   */
  public suspend fun getFilter(id: String): GetFilterResponse {
    try {
      val response = configuration.client.`get`("api/v1/filters/{id}".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> GetFilterResponseSuccess(response.body<V1Filter>())
        401, 404, 429, 503 -> GetFilterResponseFailure401(response.body<Error>())
        410 -> GetFilterResponseFailure410
        422 -> GetFilterResponseFailure(response.body<ValidationError>())
        else -> GetFilterResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return GetFilterResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class GetFilterResponse

  @Serializable
  public data class GetFilterResponseSuccess(
    public val body: V1Filter,
  ) : GetFilterResponse()

  @Serializable
  public data class GetFilterResponseFailure401(
    public val body: Error,
  ) : GetFilterResponse()

  @Serializable
  public object GetFilterResponseFailure410 : GetFilterResponse()

  @Serializable
  public data class GetFilterResponseFailure(
    public val body: ValidationError,
  ) : GetFilterResponse()

  @Serializable
  public data class GetFilterResponseUnknownFailure(
    public val statusCode: Int,
  ) : GetFilterResponse()
}
