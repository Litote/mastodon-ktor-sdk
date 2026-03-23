package org.litote.mastodon.ktor.sdk.accountsApiV1AccountsIdUnpinPost.client

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

public class AccountsApiV1AccountsIdUnpinPostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Unfeature account from profile
   */
  public suspend fun postAccountUnpin(id: String): PostAccountUnpinResponse {
    try {
      val response = configuration.client.post("api/v1/accounts/{id}/unpin".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> PostAccountUnpinResponseSuccess(response.body<Relationship>())
        401, 404, 422, 429, 503 -> PostAccountUnpinResponseFailure401(response.body<Error>())
        410 -> PostAccountUnpinResponseFailure
        else -> PostAccountUnpinResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return PostAccountUnpinResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class PostAccountUnpinResponse

  @Serializable
  public data class PostAccountUnpinResponseSuccess(
    public val body: Relationship,
  ) : PostAccountUnpinResponse()

  @Serializable
  public data class PostAccountUnpinResponseFailure401(
    public val body: Error,
  ) : PostAccountUnpinResponse()

  @Serializable
  public object PostAccountUnpinResponseFailure : PostAccountUnpinResponse()

  @Serializable
  public data class PostAccountUnpinResponseUnknownFailure(
    public val statusCode: Int,
  ) : PostAccountUnpinResponse()
}
