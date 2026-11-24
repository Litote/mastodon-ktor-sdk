package org.litote.mastodon.ktor.sdk.suggestionsApiV1SuggestionsAccountIdDelete.client

import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.http.Headers
import io.ktor.http.encodeURLPathPart
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

public interface SuggestionsApiV1SuggestionsAccountIdDeleteClient {
  /**
   * Remove a suggestion
   */
  public suspend fun deleteSuggestionsByAccountId(accountId: String): DeleteSuggestionsByAccountIdResponse

  @Serializable
  public sealed class DeleteSuggestionsByAccountIdResponse : OperationResponse<Unit> {
    abstract override val headers: Headers
  }

  @Serializable
  public data class DeleteSuggestionsByAccountIdResponseSuccess(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : DeleteSuggestionsByAccountIdResponse(),
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
  public data class DeleteSuggestionsByAccountIdResponseFailure401(
    override val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : DeleteSuggestionsByAccountIdResponse(),
      OperationResponseFailure<Error>

  @Serializable
  public data class DeleteSuggestionsByAccountIdResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : DeleteSuggestionsByAccountIdResponse(),
      OperationResponseFailure<Unit> {
    override val body: Unit
      get() = Unit
  }

  @Serializable
  public data class DeleteSuggestionsByAccountIdResponseFailure(
    override val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : DeleteSuggestionsByAccountIdResponse(),
      OperationResponseFailure<ValidationError>

  @Serializable
  public data class DeleteSuggestionsByAccountIdResponseUnknownFailure(
    override val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : DeleteSuggestionsByAccountIdResponse(),
      OperationResponseUnknownFailure
}

public fun SuggestionsApiV1SuggestionsAccountIdDeleteClient(configuration: ClientConfiguration = defaultClientConfiguration): SuggestionsApiV1SuggestionsAccountIdDeleteClient = DefaultSuggestionsApiV1SuggestionsAccountIdDeleteClient(configuration)

public class DefaultSuggestionsApiV1SuggestionsAccountIdDeleteClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : SuggestionsApiV1SuggestionsAccountIdDeleteClient {
  override suspend fun deleteSuggestionsByAccountId(accountId: String): SuggestionsApiV1SuggestionsAccountIdDeleteClient.DeleteSuggestionsByAccountIdResponse {
    try {
      val response = configuration.client.delete("api/v1/suggestions/{account_id}".replace("/{account_id}", "/${accountId.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> SuggestionsApiV1SuggestionsAccountIdDeleteClient.DeleteSuggestionsByAccountIdResponseSuccess(response.headers)
        401, 404, 429, 503 -> SuggestionsApiV1SuggestionsAccountIdDeleteClient.DeleteSuggestionsByAccountIdResponseFailure401(response.body<Error>(), response.headers)
        410 -> SuggestionsApiV1SuggestionsAccountIdDeleteClient.DeleteSuggestionsByAccountIdResponseFailure410(response.headers)
        422 -> SuggestionsApiV1SuggestionsAccountIdDeleteClient.DeleteSuggestionsByAccountIdResponseFailure(response.body<ValidationError>(), response.headers)
        else -> SuggestionsApiV1SuggestionsAccountIdDeleteClient.DeleteSuggestionsByAccountIdResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return SuggestionsApiV1SuggestionsAccountIdDeleteClient.DeleteSuggestionsByAccountIdResponseUnknownFailure(500)
    }
  }
}
