package org.litote.mastodon.ktor.sdk.accountsApiV1AccountsIdFollowPost.client

import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.contentType
import io.ktor.http.encodeURLPathPart
import kotlin.Boolean
import kotlin.Int
import kotlin.String
import kotlin.collections.List
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidblockpostBcce5a7a.model.Relationship

public interface AccountsApiV1AccountsIdFollowPostClient {
  /**
   * Follow account
   */
  public suspend fun postAccountFollow(request: PostAccountFollowRequest, id: String): PostAccountFollowResponse

  @Serializable
  public data class PostAccountFollowRequest(
    public val languages: List<String>? = null,
    public val notify: Boolean? = false,
    public val reblogs: Boolean? = true,
  )

  @Serializable
  public sealed class PostAccountFollowResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class PostAccountFollowResponseSuccess(
    public val body: Relationship,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostAccountFollowResponse() {
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
  public data class PostAccountFollowResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostAccountFollowResponse()

  @Serializable
  public data class PostAccountFollowResponseFailure(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostAccountFollowResponse()

  @Serializable
  public data class PostAccountFollowResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostAccountFollowResponse()
}

public fun AccountsApiV1AccountsIdFollowPostClient(configuration: ClientConfiguration = defaultClientConfiguration): AccountsApiV1AccountsIdFollowPostClient = DefaultAccountsApiV1AccountsIdFollowPostClient(configuration)

public class DefaultAccountsApiV1AccountsIdFollowPostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : AccountsApiV1AccountsIdFollowPostClient {
  override suspend fun postAccountFollow(request: AccountsApiV1AccountsIdFollowPostClient.PostAccountFollowRequest, id: String): AccountsApiV1AccountsIdFollowPostClient.PostAccountFollowResponse {
    try {
      val response = configuration.client.post("api/v1/accounts/{id}/follow".replace("/{id}", "/${id.encodeURLPathPart()}")) {
        setBody(request)
        contentType(ContentType.Application.Json)
      }
      return when (response.status.value) {
        200 -> AccountsApiV1AccountsIdFollowPostClient.PostAccountFollowResponseSuccess(response.body<Relationship>(), response.headers)
        401, 403, 404, 422, 429, 503 -> AccountsApiV1AccountsIdFollowPostClient.PostAccountFollowResponseFailure401(response.body<Error>(), response.headers)
        410 -> AccountsApiV1AccountsIdFollowPostClient.PostAccountFollowResponseFailure(response.headers)
        else -> AccountsApiV1AccountsIdFollowPostClient.PostAccountFollowResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return AccountsApiV1AccountsIdFollowPostClient.PostAccountFollowResponseUnknownFailure(500)
    }
  }
}
