package org.litote.mastodon.ktor.sdk.accountsApiV1AccountsIdPinPost.client

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

public class AccountsApiV1AccountsIdPinPostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Feature account on your profile
   */
  public suspend fun postAccountPin(id: String): PostAccountPinResponse {
    try {
      val response = configuration.client.post("api/v1/accounts/{id}/pin".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> PostAccountPinResponseSuccess(response.body<Relationship>())
        401, 403, 404, 422, 429, 500, 503 -> PostAccountPinResponseFailure401(response.body<Error>())
        410 -> PostAccountPinResponseFailure
        else -> PostAccountPinResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return PostAccountPinResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class PostAccountPinResponse

  @Serializable
  public data class PostAccountPinResponseSuccess(
    public val body: Relationship,
  ) : PostAccountPinResponse()

  @Serializable
  public data class PostAccountPinResponseFailure401(
    public val body: Error,
  ) : PostAccountPinResponse()

  @Serializable
  public object PostAccountPinResponseFailure : PostAccountPinResponse()

  @Serializable
  public data class PostAccountPinResponseUnknownFailure(
    public val statusCode: Int,
  ) : PostAccountPinResponse()
}
