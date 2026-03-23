package org.litote.mastodon.ktor.sdk.filtersApiV2FiltersPost.client

import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
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
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget04cf5721.model.Filter
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesgetF33cecdd.model.FilterContextEnum

public class FiltersApiV2FiltersPostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Create a filter
   */
  public suspend fun createFilterV2(request: CreateFilterV2Request): CreateFilterV2Response {
    try {
      val response = configuration.client.post("api/v2/filters") {
        setBody(request)
        contentType(ContentType.Application.Json)
      }
      return when (response.status.value) {
        200 -> CreateFilterV2ResponseSuccess(response.body<Filter>())
        401, 404, 422, 429, 503 -> CreateFilterV2ResponseFailure401(response.body<Error>())
        410 -> CreateFilterV2ResponseFailure
        else -> CreateFilterV2ResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return CreateFilterV2ResponseUnknownFailure(500)
    }
  }

  @Serializable
  public data class CreateFilterV2Request(
    public val context: List<FilterContextEnum>,
    @SerialName("expires_in")
    public val expiresIn: Long? = null,
    @SerialName("filter_action")
    public val filterAction: String? = null,
    @SerialName("keywords_attributes")
    public val keywordsAttributes: List<JsonElement>? = null,
    public val title: String,
  )

  @Serializable
  public sealed class CreateFilterV2Response

  @Serializable
  public data class CreateFilterV2ResponseSuccess(
    public val body: Filter,
  ) : CreateFilterV2Response()

  @Serializable
  public data class CreateFilterV2ResponseFailure401(
    public val body: Error,
  ) : CreateFilterV2Response()

  @Serializable
  public object CreateFilterV2ResponseFailure : CreateFilterV2Response()

  @Serializable
  public data class CreateFilterV2ResponseUnknownFailure(
    public val statusCode: Int,
  ) : CreateFilterV2Response()
}
