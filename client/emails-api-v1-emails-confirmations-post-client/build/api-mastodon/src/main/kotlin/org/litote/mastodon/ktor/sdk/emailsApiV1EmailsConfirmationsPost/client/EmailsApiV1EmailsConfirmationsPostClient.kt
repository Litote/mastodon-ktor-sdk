package org.litote.mastodon.ktor.sdk.emailsApiV1EmailsConfirmationsPost.client

import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.contentType
import kotlin.Int
import kotlin.String
import kotlin.Unit
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.client.OperationResponse
import org.litote.mastodon.ktor.sdk.client.OperationResponseFailure
import org.litote.mastodon.ktor.sdk.client.OperationResponseSuccess
import org.litote.mastodon.ktor.sdk.client.OperationResponseUnknownFailure
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError

public interface EmailsApiV1EmailsConfirmationsPostClient {
  /**
   * Resend confirmation email
   */
  public suspend fun createEmailConfirmations(request: CreateEmailConfirmationsRequest): CreateEmailConfirmationsResponse

  @Serializable
  public data class CreateEmailConfirmationsRequest(
    public val email: String? = null,
  )

  @Serializable
  public sealed class CreateEmailConfirmationsResponse : OperationResponse<Unit> {
    abstract override val headers: Headers
  }

  @Serializable
  public data class CreateEmailConfirmationsResponseSuccess(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : CreateEmailConfirmationsResponse(),
      OperationResponseSuccess<Unit> {
    override val body: Unit
      get() = Unit

    /**
     * Number of requests permitted per time period
     */
    public val xRateLimitLimit: Int?
      get() = headers["X-RateLimit-Limit"]?.toIntOrNull()

    /**
     * Number of requests you can still make
     */
    public val xRateLimitRemaining: Int?
      get() = headers["X-RateLimit-Remaining"]?.toIntOrNull()

    /**
     * Timestamp when your rate limit will reset
     */
    public val xRateLimitReset: String?
      get() = headers["X-RateLimit-Reset"]
  }

  @Serializable
  public data class CreateEmailConfirmationsResponseFailure401(
    override val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : CreateEmailConfirmationsResponse(),
      OperationResponseFailure<Error>

  @Serializable
  public data class CreateEmailConfirmationsResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : CreateEmailConfirmationsResponse(),
      OperationResponseFailure<Unit> {
    override val body: Unit
      get() = Unit
  }

  @Serializable
  public data class CreateEmailConfirmationsResponseFailure(
    override val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : CreateEmailConfirmationsResponse(),
      OperationResponseFailure<ValidationError>

  @Serializable
  public data class CreateEmailConfirmationsResponseUnknownFailure(
    override val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : CreateEmailConfirmationsResponse(),
      OperationResponseUnknownFailure
}

public fun EmailsApiV1EmailsConfirmationsPostClient(configuration: ClientConfiguration = defaultClientConfiguration): EmailsApiV1EmailsConfirmationsPostClient = DefaultEmailsApiV1EmailsConfirmationsPostClient(configuration)

public class DefaultEmailsApiV1EmailsConfirmationsPostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : EmailsApiV1EmailsConfirmationsPostClient {
  override suspend fun createEmailConfirmations(request: EmailsApiV1EmailsConfirmationsPostClient.CreateEmailConfirmationsRequest): EmailsApiV1EmailsConfirmationsPostClient.CreateEmailConfirmationsResponse {
    try {
      val response = configuration.client.post("api/v1/emails/confirmations") {
        setBody(request)
        contentType(ContentType.Application.Json)
      }
      return when (response.status.value) {
        200 -> EmailsApiV1EmailsConfirmationsPostClient.CreateEmailConfirmationsResponseSuccess(response.headers)
        401, 403, 404, 429, 503 -> EmailsApiV1EmailsConfirmationsPostClient.CreateEmailConfirmationsResponseFailure401(response.body<Error>(), response.headers)
        410 -> EmailsApiV1EmailsConfirmationsPostClient.CreateEmailConfirmationsResponseFailure410(response.headers)
        422 -> EmailsApiV1EmailsConfirmationsPostClient.CreateEmailConfirmationsResponseFailure(response.body<ValidationError>(), response.headers)
        else -> EmailsApiV1EmailsConfirmationsPostClient.CreateEmailConfirmationsResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return EmailsApiV1EmailsConfirmationsPostClient.CreateEmailConfirmationsResponseUnknownFailure(500)
    }
  }
}
