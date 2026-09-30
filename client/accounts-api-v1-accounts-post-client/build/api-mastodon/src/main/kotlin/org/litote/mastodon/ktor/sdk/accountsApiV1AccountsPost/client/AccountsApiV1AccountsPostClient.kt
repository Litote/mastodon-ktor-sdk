package org.litote.mastodon.ktor.sdk.accountsApiV1AccountsPost.client

import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.contentType
import kotlin.Boolean
import kotlin.Int
import kotlin.String
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountspostOauthoauthtokenpost.model.Token

public interface AccountsApiV1AccountsPostClient {
  /**
   * Register an account
   */
  public suspend fun createAccount(request: CreateAccountRequest): CreateAccountResponse

  @Serializable
  public data class CreateAccountRequest(
    public val agreement: Boolean,
    @SerialName("date_of_birth")
    public val dateOfBirth: String? = null,
    public val email: String,
    public val locale: String,
    public val password: String,
    public val reason: String? = null,
    public val username: String,
  )

  @Serializable
  public sealed class CreateAccountResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class CreateAccountResponseSuccess(
    public val body: Token,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : CreateAccountResponse() {
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
  public data class CreateAccountResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : CreateAccountResponse()

  @Serializable
  public data class CreateAccountResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : CreateAccountResponse()

  @Serializable
  public data class CreateAccountResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : CreateAccountResponse()

  @Serializable
  public data class CreateAccountResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : CreateAccountResponse()
}

public fun AccountsApiV1AccountsPostClient(configuration: ClientConfiguration = defaultClientConfiguration): AccountsApiV1AccountsPostClient = DefaultAccountsApiV1AccountsPostClient(configuration)

public class DefaultAccountsApiV1AccountsPostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : AccountsApiV1AccountsPostClient {
  override suspend fun createAccount(request: AccountsApiV1AccountsPostClient.CreateAccountRequest): AccountsApiV1AccountsPostClient.CreateAccountResponse {
    try {
      val response = configuration.client.post("api/v1/accounts") {
        setBody(request)
        contentType(ContentType.Application.Json)
      }
      return when (response.status.value) {
        200 -> AccountsApiV1AccountsPostClient.CreateAccountResponseSuccess(response.body<Token>(), response.headers)
        401, 404, 429, 503 -> AccountsApiV1AccountsPostClient.CreateAccountResponseFailure401(response.body<Error>(), response.headers)
        410 -> AccountsApiV1AccountsPostClient.CreateAccountResponseFailure410(response.headers)
        422 -> AccountsApiV1AccountsPostClient.CreateAccountResponseFailure(response.body<ValidationError>(), response.headers)
        else -> AccountsApiV1AccountsPostClient.CreateAccountResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return AccountsApiV1AccountsPostClient.CreateAccountResponseUnknownFailure(500)
    }
  }
}
