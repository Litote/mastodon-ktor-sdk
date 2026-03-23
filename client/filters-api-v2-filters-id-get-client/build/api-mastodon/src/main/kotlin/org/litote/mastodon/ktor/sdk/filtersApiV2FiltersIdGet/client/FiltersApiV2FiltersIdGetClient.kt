package org.litote.mastodon.ktor.sdk.filtersApiV2FiltersIdGet.client

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
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget04cf5721.model.Filter

public class FiltersApiV2FiltersIdGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * View a specific filter
   */
  public suspend fun getFilterV2(id: String): GetFilterV2Response {
    try {
      val response = configuration.client.`get`("api/v2/filters/{id}".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> GetFilterV2ResponseSuccess(response.body<Filter>())
        401, 404, 429, 503 -> GetFilterV2ResponseFailure401(response.body<Error>())
        410 -> GetFilterV2ResponseFailure410
        422 -> GetFilterV2ResponseFailure(response.body<ValidationError>())
        else -> GetFilterV2ResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return GetFilterV2ResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class GetFilterV2Response

  @Serializable
  public data class GetFilterV2ResponseSuccess(
    public val body: Filter,
  ) : GetFilterV2Response()

  @Serializable
  public data class GetFilterV2ResponseFailure401(
    public val body: Error,
  ) : GetFilterV2Response()

  @Serializable
  public object GetFilterV2ResponseFailure410 : GetFilterV2Response()

  @Serializable
  public data class GetFilterV2ResponseFailure(
    public val body: ValidationError,
  ) : GetFilterV2Response()

  @Serializable
  public data class GetFilterV2ResponseUnknownFailure(
    public val statusCode: Int,
  ) : GetFilterV2Response()
}
