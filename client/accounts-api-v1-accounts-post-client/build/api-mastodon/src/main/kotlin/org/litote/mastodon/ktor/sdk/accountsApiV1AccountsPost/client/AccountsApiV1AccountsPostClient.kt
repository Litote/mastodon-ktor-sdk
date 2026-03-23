package org.litote.mastodon.ktor.sdk.accountsApiV1AccountsPost.client

import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlin.Boolean
import kotlin.Int
import kotlin.String
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountspostOauthoauthtokenpost.model.Token

public class AccountsApiV1AccountsPostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Register an account
   */
  public suspend fun createAccount(request: CreateAccountRequest): CreateAccountResponse {
    try {
      val response = configuration.client.post("api/v1/accounts") {
        setBody(request)
        contentType(ContentType.Application.Json)
      }
      return when (response.status.value) {
        200 -> CreateAccountResponseSuccess(response.body<Token>())
        401, 404, 429, 503 -> CreateAccountResponseFailure401(response.body<Error>())
        410 -> CreateAccountResponseFailure410
        422 -> CreateAccountResponseFailure(response.body<ValidationError>())
        else -> CreateAccountResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return CreateAccountResponseUnknownFailure(500)
    }
  }

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
  public sealed class CreateAccountResponse

  @Serializable
  public data class CreateAccountResponseSuccess(
    public val body: Token,
  ) : CreateAccountResponse()

  @Serializable
  public data class CreateAccountResponseFailure401(
    public val body: Error,
  ) : CreateAccountResponse()

  @Serializable
  public object CreateAccountResponseFailure410 : CreateAccountResponse()

  @Serializable
  public data class CreateAccountResponseFailure(
    public val body: ValidationError,
  ) : CreateAccountResponse()

  @Serializable
  public data class CreateAccountResponseUnknownFailure(
    public val statusCode: Int,
  ) : CreateAccountResponse()
}
