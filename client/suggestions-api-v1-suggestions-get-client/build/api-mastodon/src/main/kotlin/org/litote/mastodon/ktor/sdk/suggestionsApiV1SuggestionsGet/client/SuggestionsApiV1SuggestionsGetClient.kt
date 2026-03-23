package org.litote.mastodon.ktor.sdk.suggestionsApiV1SuggestionsGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import kotlin.Int
import kotlin.Long
import kotlin.collections.List
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersgetB7d593a6.model.Account
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError

public class SuggestionsApiV1SuggestionsGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * View follow suggestions (v1)
   */
  public suspend fun getSuggestions(limit: Long? = 40): GetSuggestionsResponse {
    try {
      val response = configuration.client.`get`("api/v1/suggestions") {
        url {
          if (limit != null) {
            parameters.append("limit", limit.toString())
          }
        }
      }
      return when (response.status.value) {
        200 -> GetSuggestionsResponseSuccess(response.body<List<Account>>())
        401, 404, 429, 503 -> GetSuggestionsResponseFailure401(response.body<Error>())
        410 -> GetSuggestionsResponseFailure410
        422 -> GetSuggestionsResponseFailure(response.body<ValidationError>())
        else -> GetSuggestionsResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return GetSuggestionsResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class GetSuggestionsResponse

  @Serializable
  public data class GetSuggestionsResponseSuccess(
    public val body: List<Account>,
  ) : GetSuggestionsResponse()

  @Serializable
  public data class GetSuggestionsResponseFailure401(
    public val body: Error,
  ) : GetSuggestionsResponse()

  @Serializable
  public object GetSuggestionsResponseFailure410 : GetSuggestionsResponse()

  @Serializable
  public data class GetSuggestionsResponseFailure(
    public val body: ValidationError,
  ) : GetSuggestionsResponse()

  @Serializable
  public data class GetSuggestionsResponseUnknownFailure(
    public val statusCode: Int,
  ) : GetSuggestionsResponse()
}
