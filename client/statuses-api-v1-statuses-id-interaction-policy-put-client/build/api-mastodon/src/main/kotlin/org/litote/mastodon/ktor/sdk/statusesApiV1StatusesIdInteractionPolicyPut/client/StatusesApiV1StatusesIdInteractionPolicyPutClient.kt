package org.litote.mastodon.ktor.sdk.statusesApiV1StatusesIdInteractionPolicyPut.client

import io.ktor.client.call.body
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.contentType
import io.ktor.http.encodeURLPathPart
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
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget83730355.model.Status

public interface StatusesApiV1StatusesIdInteractionPolicyPutClient {
  /**
   * Edit a status' interaction policies
   */
  public suspend fun updateStatusInteractionPolicy(request: UpdateStatusInteractionPolicyRequest, id: String): UpdateStatusInteractionPolicyResponse

  @Serializable
  public data class UpdateStatusInteractionPolicyRequest(
    @SerialName("quote_approval_policy")
    public val quoteApprovalPolicy: String? = null,
  )

  @Serializable
  public sealed class UpdateStatusInteractionPolicyResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class UpdateStatusInteractionPolicyResponseSuccess(
    public val body: Status,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : UpdateStatusInteractionPolicyResponse() {
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
  public data class UpdateStatusInteractionPolicyResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : UpdateStatusInteractionPolicyResponse()

  @Serializable
  public data class UpdateStatusInteractionPolicyResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : UpdateStatusInteractionPolicyResponse()

  @Serializable
  public data class UpdateStatusInteractionPolicyResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : UpdateStatusInteractionPolicyResponse()

  @Serializable
  public data class UpdateStatusInteractionPolicyResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : UpdateStatusInteractionPolicyResponse()
}

public fun StatusesApiV1StatusesIdInteractionPolicyPutClient(configuration: ClientConfiguration = defaultClientConfiguration): StatusesApiV1StatusesIdInteractionPolicyPutClient = DefaultStatusesApiV1StatusesIdInteractionPolicyPutClient(configuration)

public class DefaultStatusesApiV1StatusesIdInteractionPolicyPutClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : StatusesApiV1StatusesIdInteractionPolicyPutClient {
  override suspend fun updateStatusInteractionPolicy(request: StatusesApiV1StatusesIdInteractionPolicyPutClient.UpdateStatusInteractionPolicyRequest, id: String): StatusesApiV1StatusesIdInteractionPolicyPutClient.UpdateStatusInteractionPolicyResponse {
    try {
      val response = configuration.client.put("api/v1/statuses/{id}/interaction_policy".replace("/{id}", "/${id.encodeURLPathPart()}")) {
        setBody(request)
        contentType(ContentType.Application.Json)
      }
      return when (response.status.value) {
        200 -> StatusesApiV1StatusesIdInteractionPolicyPutClient.UpdateStatusInteractionPolicyResponseSuccess(response.body<Status>(), response.headers)
        401, 404, 429, 503 -> StatusesApiV1StatusesIdInteractionPolicyPutClient.UpdateStatusInteractionPolicyResponseFailure401(response.body<Error>(), response.headers)
        410 -> StatusesApiV1StatusesIdInteractionPolicyPutClient.UpdateStatusInteractionPolicyResponseFailure410(response.headers)
        422 -> StatusesApiV1StatusesIdInteractionPolicyPutClient.UpdateStatusInteractionPolicyResponseFailure(response.body<ValidationError>(), response.headers)
        else -> StatusesApiV1StatusesIdInteractionPolicyPutClient.UpdateStatusInteractionPolicyResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return StatusesApiV1StatusesIdInteractionPolicyPutClient.UpdateStatusInteractionPolicyResponseUnknownFailure(500)
    }
  }
}
