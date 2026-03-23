package org.litote.mastodon.ktor.sdk.accountsApiV1AccountsIdNotePost.client

import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.encodeURLPathPart
import kotlin.Int
import kotlin.String
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidblockpostBcce5a7a.model.Relationship

public class AccountsApiV1AccountsIdNotePostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Set private note on profile
   */
  public suspend fun postAccountNote(request: PostAccountNoteRequest, id: String): PostAccountNoteResponse {
    try {
      val response = configuration.client.post("api/v1/accounts/{id}/note".replace("/{id}", "/${id.encodeURLPathPart()}")) {
        setBody(request)
        contentType(ContentType.Application.Json)
      }
      return when (response.status.value) {
        200 -> PostAccountNoteResponseSuccess(response.body<Relationship>())
        401, 404, 422, 429, 503 -> PostAccountNoteResponseFailure401(response.body<Error>())
        410 -> PostAccountNoteResponseFailure
        else -> PostAccountNoteResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return PostAccountNoteResponseUnknownFailure(500)
    }
  }

  @Serializable
  public data class PostAccountNoteRequest(
    public val comment: String? = null,
  )

  @Serializable
  public sealed class PostAccountNoteResponse

  @Serializable
  public data class PostAccountNoteResponseSuccess(
    public val body: Relationship,
  ) : PostAccountNoteResponse()

  @Serializable
  public data class PostAccountNoteResponseFailure401(
    public val body: Error,
  ) : PostAccountNoteResponse()

  @Serializable
  public object PostAccountNoteResponseFailure : PostAccountNoteResponse()

  @Serializable
  public data class PostAccountNoteResponseUnknownFailure(
    public val statusCode: Int,
  ) : PostAccountNoteResponse()
}
