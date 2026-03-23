package org.litote.mastodon.ktor.sdk.suggestionsApiV1SuggestionsAccountIdDelete.client

import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.http.encodeURLPathPart
import kotlin.Int
import kotlin.String
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError

public class SuggestionsApiV1SuggestionsAccountIdDeleteClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Remove a suggestion
   */
  public suspend fun deleteSuggestionsByAccountId(accountId: String): DeleteSuggestionsByAccountIdResponse {
    try {
      val response = configuration.client.delete("api/v1/suggestions/{account_id}".replace("/{account_id}", "/${accountId.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> DeleteSuggestionsByAccountIdResponseSuccess
        401, 404, 429, 503 -> DeleteSuggestionsByAccountIdResponseFailure401(response.body<Error>())
        410 -> DeleteSuggestionsByAccountIdResponseFailure410
        422 -> DeleteSuggestionsByAccountIdResponseFailure(response.body<ValidationError>())
        else -> DeleteSuggestionsByAccountIdResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return DeleteSuggestionsByAccountIdResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class DeleteSuggestionsByAccountIdResponse

  @Serializable
  public object DeleteSuggestionsByAccountIdResponseSuccess : DeleteSuggestionsByAccountIdResponse()

  @Serializable
  public data class DeleteSuggestionsByAccountIdResponseFailure401(
    public val body: Error,
  ) : DeleteSuggestionsByAccountIdResponse()

  @Serializable
  public object DeleteSuggestionsByAccountIdResponseFailure410 : DeleteSuggestionsByAccountIdResponse()

  @Serializable
  public data class DeleteSuggestionsByAccountIdResponseFailure(
    public val body: ValidationError,
  ) : DeleteSuggestionsByAccountIdResponse()

  @Serializable
  public data class DeleteSuggestionsByAccountIdResponseUnknownFailure(
    public val statusCode: Int,
  ) : DeleteSuggestionsByAccountIdResponse()
}
