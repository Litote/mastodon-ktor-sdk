package org.litote.mastodon.ktor.sdk.accountsApiV1AccountsIdUnendorsePost.client

import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.http.encodeURLPathPart
import kotlin.Int
import kotlin.String
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidblockpostBcce5a7a.model.Relationship

public class AccountsApiV1AccountsIdUnendorsePostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Unfeature account from profile
   */
  public suspend fun postAccountUnendorse(id: String): PostAccountUnendorseResponse {
    try {
      val response = configuration.client.post("api/v1/accounts/{id}/unendorse".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> PostAccountUnendorseResponseSuccess(response.body<Relationship>())
        401, 404, 422, 429, 503 -> PostAccountUnendorseResponseFailure401(response.body<Error>())
        410 -> PostAccountUnendorseResponseFailure
        else -> PostAccountUnendorseResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return PostAccountUnendorseResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class PostAccountUnendorseResponse

  @Serializable
  public data class PostAccountUnendorseResponseSuccess(
    public val body: Relationship,
  ) : PostAccountUnendorseResponse()

  @Serializable
  public data class PostAccountUnendorseResponseFailure401(
    public val body: Error,
  ) : PostAccountUnendorseResponse()

  @Serializable
  public object PostAccountUnendorseResponseFailure : PostAccountUnendorseResponse()

  @Serializable
  public data class PostAccountUnendorseResponseUnknownFailure(
    public val statusCode: Int,
  ) : PostAccountUnendorseResponse()
}
