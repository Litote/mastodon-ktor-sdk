package org.litote.mastodon.ktor.sdk.accountsApiV1AccountsIdNotePost.client

import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.contentType
import io.ktor.http.encodeURLPathPart
import kotlin.Int
import kotlin.String
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidblockpostBcce5a7a.model.Relationship

public interface AccountsApiV1AccountsIdNotePostClient {
  /**
   * Set private note on profile
   */
  public suspend fun postAccountNote(request: PostAccountNoteRequest, id: String): PostAccountNoteResponse

  @Serializable
  public data class PostAccountNoteRequest(
    public val comment: String? = null,
  )

  @Serializable
  public sealed class PostAccountNoteResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class PostAccountNoteResponseSuccess(
    public val body: Relationship,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostAccountNoteResponse() {
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
  public data class PostAccountNoteResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostAccountNoteResponse()

  @Serializable
  public data class PostAccountNoteResponseFailure(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostAccountNoteResponse()

  @Serializable
  public data class PostAccountNoteResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostAccountNoteResponse()
}

public fun AccountsApiV1AccountsIdNotePostClient(configuration: ClientConfiguration = defaultClientConfiguration): AccountsApiV1AccountsIdNotePostClient = DefaultAccountsApiV1AccountsIdNotePostClient(configuration)

public class DefaultAccountsApiV1AccountsIdNotePostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : AccountsApiV1AccountsIdNotePostClient {
  override suspend fun postAccountNote(request: AccountsApiV1AccountsIdNotePostClient.PostAccountNoteRequest, id: String): AccountsApiV1AccountsIdNotePostClient.PostAccountNoteResponse {
    try {
      val response = configuration.client.post("api/v1/accounts/{id}/note".replace("/{id}", "/${id.encodeURLPathPart()}")) {
        setBody(request)
        contentType(ContentType.Application.Json)
      }
      return when (response.status.value) {
        200 -> AccountsApiV1AccountsIdNotePostClient.PostAccountNoteResponseSuccess(response.body<Relationship>(), response.headers)
        401, 404, 422, 429, 503 -> AccountsApiV1AccountsIdNotePostClient.PostAccountNoteResponseFailure401(response.body<Error>(), response.headers)
        410 -> AccountsApiV1AccountsIdNotePostClient.PostAccountNoteResponseFailure(response.headers)
        else -> AccountsApiV1AccountsIdNotePostClient.PostAccountNoteResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return AccountsApiV1AccountsIdNotePostClient.PostAccountNoteResponseUnknownFailure(500)
    }
  }
}
