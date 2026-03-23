package org.litote.mastodon.ktor.sdk.emailsApiV1EmailsConfirmationsPost.client

import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlin.Int
import kotlin.String
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError

public class EmailsApiV1EmailsConfirmationsPostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Resend confirmation email
   */
  public suspend fun createEmailConfirmations(request: CreateEmailConfirmationsRequest): CreateEmailConfirmationsResponse {
    try {
      val response = configuration.client.post("api/v1/emails/confirmations") {
        setBody(request)
        contentType(ContentType.Application.Json)
      }
      return when (response.status.value) {
        200 -> CreateEmailConfirmationsResponseSuccess
        401, 403, 404, 429, 503 -> CreateEmailConfirmationsResponseFailure401(response.body<Error>())
        410 -> CreateEmailConfirmationsResponseFailure410
        422 -> CreateEmailConfirmationsResponseFailure(response.body<ValidationError>())
        else -> CreateEmailConfirmationsResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return CreateEmailConfirmationsResponseUnknownFailure(500)
    }
  }

  @Serializable
  public data class CreateEmailConfirmationsRequest(
    public val email: String? = null,
  )

  @Serializable
  public sealed class CreateEmailConfirmationsResponse

  @Serializable
  public object CreateEmailConfirmationsResponseSuccess : CreateEmailConfirmationsResponse()

  @Serializable
  public data class CreateEmailConfirmationsResponseFailure401(
    public val body: Error,
  ) : CreateEmailConfirmationsResponse()

  @Serializable
  public object CreateEmailConfirmationsResponseFailure410 : CreateEmailConfirmationsResponse()

  @Serializable
  public data class CreateEmailConfirmationsResponseFailure(
    public val body: ValidationError,
  ) : CreateEmailConfirmationsResponse()

  @Serializable
  public data class CreateEmailConfirmationsResponseUnknownFailure(
    public val statusCode: Int,
  ) : CreateEmailConfirmationsResponse()
}
