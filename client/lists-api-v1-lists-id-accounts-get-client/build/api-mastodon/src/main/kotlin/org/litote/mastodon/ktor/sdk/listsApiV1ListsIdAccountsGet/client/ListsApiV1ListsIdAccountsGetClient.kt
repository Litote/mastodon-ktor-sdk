package org.litote.mastodon.ktor.sdk.listsApiV1ListsIdAccountsGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import io.ktor.http.encodeURLPathPart
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.collections.List
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersgetB7d593a6.model.Account
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError

public class ListsApiV1ListsIdAccountsGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * View accounts in a list
   */
  public suspend fun getListAccounts(
    id: String,
    limit: Long? = 40,
    maxId: String? = null,
    minId: String? = null,
    sinceId: String? = null,
  ): GetListAccountsResponse {
    try {
      val response = configuration.client.`get`("api/v1/lists/{id}/accounts".replace("/{id}", "/${id.encodeURLPathPart()}")) {
        url {
          if (limit != null) {
            parameters.append("limit", limit.toString())
          }
          if (maxId != null) {
            parameters.append("max_id", maxId)
          }
          if (minId != null) {
            parameters.append("min_id", minId)
          }
          if (sinceId != null) {
            parameters.append("since_id", sinceId)
          }
        }
      }
      return when (response.status.value) {
        200 -> GetListAccountsResponseSuccess(response.body<List<Account>>())
        401, 404, 429, 503 -> GetListAccountsResponseFailure401(response.body<Error>())
        410 -> GetListAccountsResponseFailure410
        422 -> GetListAccountsResponseFailure(response.body<ValidationError>())
        else -> GetListAccountsResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return GetListAccountsResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class GetListAccountsResponse

  @Serializable
  public data class GetListAccountsResponseSuccess(
    public val body: List<Account>,
  ) : GetListAccountsResponse()

  @Serializable
  public data class GetListAccountsResponseFailure401(
    public val body: Error,
  ) : GetListAccountsResponse()

  @Serializable
  public object GetListAccountsResponseFailure410 : GetListAccountsResponse()

  @Serializable
  public data class GetListAccountsResponseFailure(
    public val body: ValidationError,
  ) : GetListAccountsResponse()

  @Serializable
  public data class GetListAccountsResponseUnknownFailure(
    public val statusCode: Int,
  ) : GetListAccountsResponse()
}
