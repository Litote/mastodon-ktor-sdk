package org.litote.mastodon.ktor.sdk.listsApiV1ListsIdGet.client

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
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidlistsgetDaf64318.model.List

public class ListsApiV1ListsIdGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Show a single list
   */
  public suspend fun getList(id: String): GetListResponse {
    try {
      val response = configuration.client.`get`("api/v1/lists/{id}".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> GetListResponseSuccess(response.body<List>())
        401, 404, 429, 503 -> GetListResponseFailure401(response.body<Error>())
        410 -> GetListResponseFailure410
        422 -> GetListResponseFailure(response.body<ValidationError>())
        else -> GetListResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return GetListResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class GetListResponse

  @Serializable
  public data class GetListResponseSuccess(
    public val body: List,
  ) : GetListResponse()

  @Serializable
  public data class GetListResponseFailure401(
    public val body: Error,
  ) : GetListResponse()

  @Serializable
  public object GetListResponseFailure410 : GetListResponse()

  @Serializable
  public data class GetListResponseFailure(
    public val body: ValidationError,
  ) : GetListResponse()

  @Serializable
  public data class GetListResponseUnknownFailure(
    public val statusCode: Int,
  ) : GetListResponse()
}
