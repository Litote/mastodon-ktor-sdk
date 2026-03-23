package org.litote.mastodon.ktor.sdk.suggestionsApiV2SuggestionsGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import kotlin.Int
import kotlin.Long
import kotlin.collections.List
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.model.Suggestion
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError

public class SuggestionsApiV2SuggestionsGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * View follow suggestions (v2)
   */
  public suspend fun getSuggestionsV2(limit: Long? = 40): GetSuggestionsV2Response {
    try {
      val response = configuration.client.`get`("api/v2/suggestions") {
        url {
          if (limit != null) {
            parameters.append("limit", limit.toString())
          }
        }
      }
      return when (response.status.value) {
        200 -> GetSuggestionsV2ResponseSuccess(response.body<List<Suggestion>>())
        401, 404, 429, 503 -> GetSuggestionsV2ResponseFailure401(response.body<Error>())
        410 -> GetSuggestionsV2ResponseFailure410
        422 -> GetSuggestionsV2ResponseFailure(response.body<ValidationError>())
        else -> GetSuggestionsV2ResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return GetSuggestionsV2ResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class GetSuggestionsV2Response

  @Serializable
  public data class GetSuggestionsV2ResponseSuccess(
    public val body: List<Suggestion>,
  ) : GetSuggestionsV2Response()

  @Serializable
  public data class GetSuggestionsV2ResponseFailure401(
    public val body: Error,
  ) : GetSuggestionsV2Response()

  @Serializable
  public object GetSuggestionsV2ResponseFailure410 : GetSuggestionsV2Response()

  @Serializable
  public data class GetSuggestionsV2ResponseFailure(
    public val body: ValidationError,
  ) : GetSuggestionsV2Response()

  @Serializable
  public data class GetSuggestionsV2ResponseUnknownFailure(
    public val statusCode: Int,
  ) : GetSuggestionsV2Response()
}
