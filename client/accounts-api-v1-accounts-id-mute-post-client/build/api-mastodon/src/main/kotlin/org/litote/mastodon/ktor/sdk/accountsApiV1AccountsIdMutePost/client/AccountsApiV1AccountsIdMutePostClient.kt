package org.litote.mastodon.ktor.sdk.accountsApiV1AccountsIdMutePost.client

import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.encodeURLPathPart
import kotlin.Boolean
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidblockpostBcce5a7a.model.Relationship

public class AccountsApiV1AccountsIdMutePostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Mute account
   */
  public suspend fun postAccountMute(request: PostAccountMuteRequest, id: String): PostAccountMuteResponse {
    try {
      val response = configuration.client.post("api/v1/accounts/{id}/mute".replace("/{id}", "/${id.encodeURLPathPart()}")) {
        setBody(request)
        contentType(ContentType.Application.Json)
      }
      return when (response.status.value) {
        200 -> PostAccountMuteResponseSuccess(response.body<Relationship>())
        401, 404, 422, 429, 503 -> PostAccountMuteResponseFailure401(response.body<Error>())
        410 -> PostAccountMuteResponseFailure
        else -> PostAccountMuteResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return PostAccountMuteResponseUnknownFailure(500)
    }
  }

  @Serializable
  public data class PostAccountMuteRequest(
    public val duration: Long? = 0,
    public val notifications: Boolean? = true,
  )

  @Serializable
  public sealed class PostAccountMuteResponse

  @Serializable
  public data class PostAccountMuteResponseSuccess(
    public val body: Relationship,
  ) : PostAccountMuteResponse()

  @Serializable
  public data class PostAccountMuteResponseFailure401(
    public val body: Error,
  ) : PostAccountMuteResponse()

  @Serializable
  public object PostAccountMuteResponseFailure : PostAccountMuteResponse()

  @Serializable
  public data class PostAccountMuteResponseUnknownFailure(
    public val statusCode: Int,
  ) : PostAccountMuteResponse()
}
