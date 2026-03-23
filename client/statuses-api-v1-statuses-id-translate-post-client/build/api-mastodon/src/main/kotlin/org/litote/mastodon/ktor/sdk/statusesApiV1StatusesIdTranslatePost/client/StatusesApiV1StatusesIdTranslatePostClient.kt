package org.litote.mastodon.ktor.sdk.statusesApiV1StatusesIdTranslatePost.client

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
import org.litote.mastodon.ktor.sdk.model.Translation
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError

public class StatusesApiV1StatusesIdTranslatePostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Translate a status
   */
  public suspend fun postStatusTranslate(request: PostStatusTranslateRequest, id: String): PostStatusTranslateResponse {
    try {
      val response = configuration.client.post("api/v1/statuses/{id}/translate".replace("/{id}", "/${id.encodeURLPathPart()}")) {
        setBody(request)
        contentType(ContentType.Application.Json)
      }
      return when (response.status.value) {
        200 -> PostStatusTranslateResponseSuccess(response.body<Translation>())
        401, 403, 404, 429, 503 -> PostStatusTranslateResponseFailure401(response.body<Error>())
        410 -> PostStatusTranslateResponseFailure410
        422 -> PostStatusTranslateResponseFailure(response.body<ValidationError>())
        else -> PostStatusTranslateResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return PostStatusTranslateResponseUnknownFailure(500)
    }
  }

  @Serializable
  public data class PostStatusTranslateRequest(
    public val lang: String? = null,
  )

  @Serializable
  public sealed class PostStatusTranslateResponse

  @Serializable
  public data class PostStatusTranslateResponseSuccess(
    public val body: Translation,
  ) : PostStatusTranslateResponse()

  @Serializable
  public data class PostStatusTranslateResponseFailure401(
    public val body: Error,
  ) : PostStatusTranslateResponse()

  @Serializable
  public object PostStatusTranslateResponseFailure410 : PostStatusTranslateResponse()

  @Serializable
  public data class PostStatusTranslateResponseFailure(
    public val body: ValidationError,
  ) : PostStatusTranslateResponse()

  @Serializable
  public data class PostStatusTranslateResponseUnknownFailure(
    public val statusCode: Int,
  ) : PostStatusTranslateResponse()
}
