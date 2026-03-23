package org.litote.mastodon.ktor.sdk.accountsApiV1AccountsIdFollowPost.client

import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.encodeURLPathPart
import kotlin.Boolean
import kotlin.Int
import kotlin.String
import kotlin.collections.List
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidblockpostBcce5a7a.model.Relationship

public class AccountsApiV1AccountsIdFollowPostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Follow account
   */
  public suspend fun postAccountFollow(request: PostAccountFollowRequest, id: String): PostAccountFollowResponse {
    try {
      val response = configuration.client.post("api/v1/accounts/{id}/follow".replace("/{id}", "/${id.encodeURLPathPart()}")) {
        setBody(request)
        contentType(ContentType.Application.Json)
      }
      return when (response.status.value) {
        200 -> PostAccountFollowResponseSuccess(response.body<Relationship>())
        401, 403, 404, 422, 429, 503 -> PostAccountFollowResponseFailure401(response.body<Error>())
        410 -> PostAccountFollowResponseFailure
        else -> PostAccountFollowResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return PostAccountFollowResponseUnknownFailure(500)
    }
  }

  @Serializable
  public data class PostAccountFollowRequest(
    public val languages: List<String>? = null,
    public val notify: Boolean? = false,
    public val reblogs: Boolean? = true,
  )

  @Serializable
  public sealed class PostAccountFollowResponse

  @Serializable
  public data class PostAccountFollowResponseSuccess(
    public val body: Relationship,
  ) : PostAccountFollowResponse()

  @Serializable
  public data class PostAccountFollowResponseFailure401(
    public val body: Error,
  ) : PostAccountFollowResponse()

  @Serializable
  public object PostAccountFollowResponseFailure : PostAccountFollowResponse()

  @Serializable
  public data class PostAccountFollowResponseUnknownFailure(
    public val statusCode: Int,
  ) : PostAccountFollowResponse()
}
