package org.litote.mastodon.ktor.sdk.statusesApiV1StatusesIdReblogPost.client

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
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget4016b7e9.model.StatusVisibilityEnum
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget83730355.model.Status

public class StatusesApiV1StatusesIdReblogPostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Boost a status
   */
  public suspend fun postStatusReblog(request: PostStatusReblogRequest, id: String): PostStatusReblogResponse {
    try {
      val response = configuration.client.post("api/v1/statuses/{id}/reblog".replace("/{id}", "/${id.encodeURLPathPart()}")) {
        setBody(request)
        contentType(ContentType.Application.Json)
      }
      return when (response.status.value) {
        200 -> PostStatusReblogResponseSuccess(response.body<Status>())
        401, 404, 429, 503 -> PostStatusReblogResponseFailure401(response.body<Error>())
        410 -> PostStatusReblogResponseFailure410
        422 -> PostStatusReblogResponseFailure(response.body<ValidationError>())
        else -> PostStatusReblogResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return PostStatusReblogResponseUnknownFailure(500)
    }
  }

  @Serializable
  public data class PostStatusReblogRequest(
    public val visibility: StatusVisibilityEnum? = null,
  )

  @Serializable
  public sealed class PostStatusReblogResponse

  @Serializable
  public data class PostStatusReblogResponseSuccess(
    public val body: Status,
  ) : PostStatusReblogResponse()

  @Serializable
  public data class PostStatusReblogResponseFailure401(
    public val body: Error,
  ) : PostStatusReblogResponse()

  @Serializable
  public object PostStatusReblogResponseFailure410 : PostStatusReblogResponse()

  @Serializable
  public data class PostStatusReblogResponseFailure(
    public val body: ValidationError,
  ) : PostStatusReblogResponse()

  @Serializable
  public data class PostStatusReblogResponseUnknownFailure(
    public val statusCode: Int,
  ) : PostStatusReblogResponse()
}
