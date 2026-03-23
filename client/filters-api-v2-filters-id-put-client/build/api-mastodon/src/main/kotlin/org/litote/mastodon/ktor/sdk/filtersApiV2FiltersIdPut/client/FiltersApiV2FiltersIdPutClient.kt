package org.litote.mastodon.ktor.sdk.filtersApiV2FiltersIdPut.client

import io.ktor.client.call.body
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.encodeURLPathPart
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.collections.List
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget04cf5721.model.Filter
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesgetF33cecdd.model.FilterContextEnum

public class FiltersApiV2FiltersIdPutClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Update a filter
   */
  public suspend fun updateFilterV2(request: UpdateFilterV2Request, id: String): UpdateFilterV2Response {
    try {
      val response = configuration.client.put("api/v2/filters/{id}".replace("/{id}", "/${id.encodeURLPathPart()}")) {
        setBody(request)
        contentType(ContentType.Application.Json)
      }
      return when (response.status.value) {
        200 -> UpdateFilterV2ResponseSuccess(response.body<Filter>())
        401, 404, 429, 503 -> UpdateFilterV2ResponseFailure401(response.body<Error>())
        410 -> UpdateFilterV2ResponseFailure410
        422 -> UpdateFilterV2ResponseFailure(response.body<ValidationError>())
        else -> UpdateFilterV2ResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return UpdateFilterV2ResponseUnknownFailure(500)
    }
  }

  @Serializable
  public data class UpdateFilterV2Request(
    public val context: List<FilterContextEnum>? = null,
    @SerialName("expires_in")
    public val expiresIn: Long? = null,
    @SerialName("filter_action")
    public val filterAction: String? = null,
    @SerialName("keywords_attributes")
    public val keywordsAttributes: List<JsonElement>? = null,
    public val title: String? = null,
  )

  @Serializable
  public sealed class UpdateFilterV2Response

  @Serializable
  public data class UpdateFilterV2ResponseSuccess(
    public val body: Filter,
  ) : UpdateFilterV2Response()

  @Serializable
  public data class UpdateFilterV2ResponseFailure401(
    public val body: Error,
  ) : UpdateFilterV2Response()

  @Serializable
  public object UpdateFilterV2ResponseFailure410 : UpdateFilterV2Response()

  @Serializable
  public data class UpdateFilterV2ResponseFailure(
    public val body: ValidationError,
  ) : UpdateFilterV2Response()

  @Serializable
  public data class UpdateFilterV2ResponseUnknownFailure(
    public val statusCode: Int,
  ) : UpdateFilterV2Response()
}
