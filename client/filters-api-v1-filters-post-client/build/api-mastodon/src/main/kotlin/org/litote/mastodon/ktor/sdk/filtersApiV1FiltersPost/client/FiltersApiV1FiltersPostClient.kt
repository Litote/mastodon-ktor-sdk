package org.litote.mastodon.ktor.sdk.filtersApiV1FiltersPost.client

import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
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

public class FiltersApiV1FiltersPostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Create a filter
   */
  public suspend fun createFilter(request: CreateFilterRequest): CreateFilterResponse {
    try {
      val response = configuration.client.post("api/v1/filters") {
        setBody(request)
        contentType(ContentType.Application.Json)
      }
      return when (response.status.value) {
        200 -> CreateFilterResponseSuccess(response.body<V1Filter>())
        401, 404, 422, 429, 503 -> CreateFilterResponseFailure401(response.body<Error>())
        410 -> CreateFilterResponseFailure
        else -> CreateFilterResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return CreateFilterResponseUnknownFailure(500)
    }
  }

  @Serializable
  public data class CreateFilterRequest(
    public val context: List<FilterContextEnum>,
    @SerialName("expires_in")
    public val expiresIn: Long? = null,
    public val irreversible: Boolean? = false,
    public val phrase: String,
    @SerialName("whole_word")
    public val wholeWord: Boolean? = false,
  )

  @Serializable
  public sealed class CreateFilterResponse

  @Serializable
  public data class CreateFilterResponseSuccess(
    public val body: V1Filter,
  ) : CreateFilterResponse()

  @Serializable
  public data class CreateFilterResponseFailure401(
    public val body: Error,
  ) : CreateFilterResponse()

  @Serializable
  public object CreateFilterResponseFailure : CreateFilterResponse()

  @Serializable
  public data class CreateFilterResponseUnknownFailure(
    public val statusCode: Int,
  ) : CreateFilterResponse()
}
