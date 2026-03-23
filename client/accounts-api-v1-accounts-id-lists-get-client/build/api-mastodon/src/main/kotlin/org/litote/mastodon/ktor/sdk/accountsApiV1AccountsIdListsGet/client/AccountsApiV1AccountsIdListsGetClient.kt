package org.litote.mastodon.ktor.sdk.accountsApiV1AccountsIdListsGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import io.ktor.http.encodeURLPathPart
import kotlin.Int
import kotlin.String
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import kotlin.collections.List as CollectionsList
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidlistsgetDaf64318.model.List as ModelList

public class AccountsApiV1AccountsIdListsGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Get lists containing this account
   */
  public suspend fun getAccountLists(id: String): GetAccountListsResponse {
    try {
      val response = configuration.client.`get`("api/v1/accounts/{id}/lists".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> GetAccountListsResponseSuccess(response.body<CollectionsList<ModelList>>())
        401, 404, 422, 429, 503 -> GetAccountListsResponseFailure401(response.body<Error>())
        410 -> GetAccountListsResponseFailure
        else -> GetAccountListsResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return GetAccountListsResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class GetAccountListsResponse

  @Serializable
  public data class GetAccountListsResponseSuccess(
    public val body: CollectionsList<ModelList>,
  ) : GetAccountListsResponse()

  @Serializable
  public data class GetAccountListsResponseFailure401(
    public val body: Error,
  ) : GetAccountListsResponse()

  @Serializable
  public object GetAccountListsResponseFailure : GetAccountListsResponse()

  @Serializable
  public data class GetAccountListsResponseUnknownFailure(
    public val statusCode: Int,
  ) : GetAccountListsResponse()
}
