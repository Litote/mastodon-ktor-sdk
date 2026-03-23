package org.litote.mastodon.ktor.sdk.accountsApiV1AccountsIdEndorsePost.client

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

public class AccountsApiV1AccountsIdEndorsePostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Feature account on your profile
   */
  public suspend fun postAccountEndorse(id: String): PostAccountEndorseResponse {
    try {
      val response = configuration.client.post("api/v1/accounts/{id}/endorse".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> PostAccountEndorseResponseSuccess(response.body<Relationship>())
        401, 403, 404, 422, 429, 500, 503 -> PostAccountEndorseResponseFailure401(response.body<Error>())
        410 -> PostAccountEndorseResponseFailure
        else -> PostAccountEndorseResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return PostAccountEndorseResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class PostAccountEndorseResponse

  @Serializable
  public data class PostAccountEndorseResponseSuccess(
    public val body: Relationship,
  ) : PostAccountEndorseResponse()

  @Serializable
  public data class PostAccountEndorseResponseFailure401(
    public val body: Error,
  ) : PostAccountEndorseResponse()

  @Serializable
  public object PostAccountEndorseResponseFailure : PostAccountEndorseResponse()

  @Serializable
  public data class PostAccountEndorseResponseUnknownFailure(
    public val statusCode: Int,
  ) : PostAccountEndorseResponse()
}
