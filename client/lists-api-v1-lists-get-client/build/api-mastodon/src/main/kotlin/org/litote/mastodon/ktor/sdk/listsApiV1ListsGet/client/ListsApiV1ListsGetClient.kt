package org.litote.mastodon.ktor.sdk.listsApiV1ListsGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import kotlin.Int
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError
import kotlin.collections.List as CollectionsList
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidlistsgetDaf64318.model.List as ModelList

public class ListsApiV1ListsGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * View your lists
   */
  public suspend fun getLists(): GetListsResponse {
    try {
      val response = configuration.client.`get`("api/v1/lists") {
      }
      return when (response.status.value) {
        200 -> GetListsResponseSuccess(response.body<CollectionsList<ModelList>>())
        401, 404, 429, 503 -> GetListsResponseFailure401(response.body<Error>())
        410 -> GetListsResponseFailure410
        422 -> GetListsResponseFailure(response.body<ValidationError>())
        else -> GetListsResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return GetListsResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class GetListsResponse

  @Serializable
  public data class GetListsResponseSuccess(
    public val body: CollectionsList<ModelList>,
  ) : GetListsResponse()

  @Serializable
  public data class GetListsResponseFailure401(
    public val body: Error,
  ) : GetListsResponse()

  @Serializable
  public object GetListsResponseFailure410 : GetListsResponse()

  @Serializable
  public data class GetListsResponseFailure(
    public val body: ValidationError,
  ) : GetListsResponse()

  @Serializable
  public data class GetListsResponseUnknownFailure(
    public val statusCode: Int,
  ) : GetListsResponse()
}
