package org.litote.mastodon.ktor.sdk.filtersApiV1FiltersIdPut.client

import io.ktor.client.call.body
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.encodeURLPathPart
import kotlin.Boolean
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.collections.List
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesgetF33cecdd.model.FilterContextEnum
import org.litote.mastodon.ktor.sdk.sharedFiltersapiv1filtersget35506993.model.V1Filter

public class FiltersApiV1FiltersIdPutClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Update a filter
   */
  public suspend fun updateFilter(request: UpdateFilterRequest, id: String): UpdateFilterResponse {
    try {
      val response = configuration.client.put("api/v1/filters/{id}".replace("/{id}", "/${id.encodeURLPathPart()}")) {
        setBody(request)
        contentType(ContentType.Application.Json)
      }
      return when (response.status.value) {
        200 -> UpdateFilterResponseSuccess(response.body<V1Filter>())
        401, 404, 422, 429, 503 -> UpdateFilterResponseFailure401(response.body<Error>())
        410 -> UpdateFilterResponseFailure
        else -> UpdateFilterResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return UpdateFilterResponseUnknownFailure(500)
    }
  }

  @Serializable
  public data class UpdateFilterRequest(
    public val context: List<FilterContextEnum>,
    @SerialName("expires_in")
    public val expiresIn: Long? = null,
    public val irreversible: Boolean? = false,
    public val phrase: String,
    @SerialName("whole_word")
    public val wholeWord: Boolean? = false,
  )

  @Serializable
  public sealed class UpdateFilterResponse

  @Serializable
  public data class UpdateFilterResponseSuccess(
    public val body: V1Filter,
  ) : UpdateFilterResponse()

  @Serializable
  public data class UpdateFilterResponseFailure401(
    public val body: Error,
  ) : UpdateFilterResponse()

  @Serializable
  public object UpdateFilterResponseFailure : UpdateFilterResponse()

  @Serializable
  public data class UpdateFilterResponseUnknownFailure(
    public val statusCode: Int,
  ) : UpdateFilterResponse()
}
