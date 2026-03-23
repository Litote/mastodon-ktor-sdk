package org.litote.mastodon.ktor.sdk.accountsApiV1AccountsSearchGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import kotlin.Boolean
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

public class AccountsApiV1AccountsSearchGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Search for matching accounts
   */
  public suspend fun getAccountSearch(
    q: String,
    following: Boolean? = false,
    limit: Long? = 40,
    offset: Long? = null,
    resolve: Boolean? = false,
  ): GetAccountSearchResponse {
    try {
      val response = configuration.client.`get`("api/v1/accounts/search") {
        url {
          parameters.append("q", q)
          if (following != null) {
            parameters.append("following", following.toString())
          }
          if (limit != null) {
            parameters.append("limit", limit.toString())
          }
          if (offset != null) {
            parameters.append("offset", offset.toString())
          }
          if (resolve != null) {
            parameters.append("resolve", resolve.toString())
          }
        }
      }
      return when (response.status.value) {
        200 -> GetAccountSearchResponseSuccess(response.body<List<Account>>())
        401, 404, 429, 503 -> GetAccountSearchResponseFailure401(response.body<Error>())
        410 -> GetAccountSearchResponseFailure410
        422 -> GetAccountSearchResponseFailure(response.body<ValidationError>())
        else -> GetAccountSearchResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return GetAccountSearchResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class GetAccountSearchResponse

  @Serializable
  public data class GetAccountSearchResponseSuccess(
    public val body: List<Account>,
  ) : GetAccountSearchResponse()

  @Serializable
  public data class GetAccountSearchResponseFailure401(
    public val body: Error,
  ) : GetAccountSearchResponse()

  @Serializable
  public object GetAccountSearchResponseFailure410 : GetAccountSearchResponse()

  @Serializable
  public data class GetAccountSearchResponseFailure(
    public val body: ValidationError,
  ) : GetAccountSearchResponse()

  @Serializable
  public data class GetAccountSearchResponseUnknownFailure(
    public val statusCode: Int,
  ) : GetAccountSearchResponse()
}
