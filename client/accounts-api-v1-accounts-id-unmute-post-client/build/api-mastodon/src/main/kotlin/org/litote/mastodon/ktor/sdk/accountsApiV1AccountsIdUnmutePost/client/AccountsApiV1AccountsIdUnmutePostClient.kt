package org.litote.mastodon.ktor.sdk.accountsApiV1AccountsIdUnmutePost.client

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

public class AccountsApiV1AccountsIdUnmutePostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Unmute account
   */
  public suspend fun postAccountUnmute(id: String): PostAccountUnmuteResponse {
    try {
      val response = configuration.client.post("api/v1/accounts/{id}/unmute".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> PostAccountUnmuteResponseSuccess(response.body<Relationship>())
        401, 404, 422, 429, 503 -> PostAccountUnmuteResponseFailure401(response.body<Error>())
        410 -> PostAccountUnmuteResponseFailure
        else -> PostAccountUnmuteResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return PostAccountUnmuteResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class PostAccountUnmuteResponse

  @Serializable
  public data class PostAccountUnmuteResponseSuccess(
    public val body: Relationship,
  ) : PostAccountUnmuteResponse()

  @Serializable
  public data class PostAccountUnmuteResponseFailure401(
    public val body: Error,
  ) : PostAccountUnmuteResponse()

  @Serializable
  public object PostAccountUnmuteResponseFailure : PostAccountUnmuteResponse()

  @Serializable
  public data class PostAccountUnmuteResponseUnknownFailure(
    public val statusCode: Int,
  ) : PostAccountUnmuteResponse()
}
