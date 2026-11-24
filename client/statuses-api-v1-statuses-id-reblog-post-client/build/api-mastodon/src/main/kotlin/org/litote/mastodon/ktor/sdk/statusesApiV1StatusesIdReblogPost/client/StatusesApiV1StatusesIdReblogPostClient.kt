package org.litote.mastodon.ktor.sdk.statusesApiV1StatusesIdReblogPost.client

import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.contentType
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
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget4016b7e9.model.StatusVisibilityEnum
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget83730355.model.Status

public interface StatusesApiV1StatusesIdReblogPostClient {
  /**
   * Boost a status
   */
  public suspend fun postStatusReblog(request: PostStatusReblogRequest, id: String): PostStatusReblogResponse

  @Serializable
  public data class PostStatusReblogRequest(
    public val visibility: StatusVisibilityEnum? = null,
  )

  @Serializable
  public sealed class PostStatusReblogResponse : OperationResponse<Status> {
    abstract override val headers: Headers
  }

  @Serializable
  public data class PostStatusReblogResponseSuccess(
    override val body: Status,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostStatusReblogResponse(),
      OperationResponseSuccess<Status> {
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
  public data class PostStatusReblogResponseFailure401(
    override val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostStatusReblogResponse(),
      OperationResponseFailure<Error>

  @Serializable
  public data class PostStatusReblogResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostStatusReblogResponse(),
      OperationResponseFailure<Unit> {
    override val body: Unit
      get() = Unit
  }

  @Serializable
  public data class PostStatusReblogResponseFailure(
    override val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostStatusReblogResponse(),
      OperationResponseFailure<ValidationError>

  @Serializable
  public data class PostStatusReblogResponseUnknownFailure(
    override val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : PostStatusReblogResponse(),
      OperationResponseUnknownFailure
}

public fun StatusesApiV1StatusesIdReblogPostClient(configuration: ClientConfiguration = defaultClientConfiguration): StatusesApiV1StatusesIdReblogPostClient = DefaultStatusesApiV1StatusesIdReblogPostClient(configuration)

public class DefaultStatusesApiV1StatusesIdReblogPostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : StatusesApiV1StatusesIdReblogPostClient {
  override suspend fun postStatusReblog(request: StatusesApiV1StatusesIdReblogPostClient.PostStatusReblogRequest, id: String): StatusesApiV1StatusesIdReblogPostClient.PostStatusReblogResponse {
    try {
      val response = configuration.client.post("api/v1/statuses/{id}/reblog".replace("/{id}", "/${id.encodeURLPathPart()}")) {
        setBody(request)
        contentType(ContentType.Application.Json)
      }
      return when (response.status.value) {
        200 -> StatusesApiV1StatusesIdReblogPostClient.PostStatusReblogResponseSuccess(response.body<Status>(), response.headers)
        401, 404, 429, 503 -> StatusesApiV1StatusesIdReblogPostClient.PostStatusReblogResponseFailure401(response.body<Error>(), response.headers)
        410 -> StatusesApiV1StatusesIdReblogPostClient.PostStatusReblogResponseFailure410(response.headers)
        422 -> StatusesApiV1StatusesIdReblogPostClient.PostStatusReblogResponseFailure(response.body<ValidationError>(), response.headers)
        else -> StatusesApiV1StatusesIdReblogPostClient.PostStatusReblogResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return StatusesApiV1StatusesIdReblogPostClient.PostStatusReblogResponseUnknownFailure(500)
    }
  }
}
