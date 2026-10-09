package org.litote.mastodon.ktor.sdk.api.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import io.ktor.client.request.delete
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.contentType
import io.ktor.http.encodeURLPathPart
import kotlin.Boolean
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.collections.List
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.JsonElement
import org.litote.mastodon.ktor.sdk.api.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.api.model.CreateStatusRequest
import org.litote.mastodon.ktor.sdk.api.model.CreateStatusResponse
import org.litote.mastodon.ktor.sdk.api.model.Error
import org.litote.mastodon.ktor.sdk.api.model.Status
import org.litote.mastodon.ktor.sdk.api.model.ValidationError
import io.ktor.client.request.`header` as setHeader

public interface StatusesClient {
  /**
   * View multiple statuses
   */
  public suspend fun getStatuses(id: List<String>? = null): GetStatusesResponse

  /**
   * Post a new status
   */
  public suspend fun createStatus(request: CreateStatusRequest, idempotencyKey: JsonElement? = null): CreateStatusResponse

  /**
   * View a single status
   */
  public suspend fun getStatus(id: String): GetStatusResponse

  /**
   * Edit a status
   */
  public suspend fun updateStatus(request: UpdateStatusRequest, id: String): UpdateStatusResponse

  /**
   * Delete a status
   */
  public suspend fun deleteStatus(id: String, deleteMedia: Boolean? = null): DeleteStatusResponse

  @Serializable
  public sealed class GetStatusesResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class GetStatusesResponseSuccess(
    public val body: List<Status>,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetStatusesResponse() {
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
  public data class GetStatusesResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetStatusesResponse()

  @Serializable
  public data class GetStatusesResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetStatusesResponse()

  @Serializable
  public data class GetStatusesResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetStatusesResponse()

  @Serializable
  public data class GetStatusesResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetStatusesResponse()

  @Serializable
  public sealed class CreateStatusResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class CreateStatusResponseSuccess(
    public val body: org.litote.mastodon.ktor.sdk.api.model.CreateStatusResponse,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : CreateStatusResponse() {
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
  public data class CreateStatusResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : CreateStatusResponse()

  @Serializable
  public data class CreateStatusResponseFailure(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : CreateStatusResponse()

  @Serializable
  public data class CreateStatusResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : CreateStatusResponse()

  @Serializable
  public sealed class GetStatusResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class GetStatusResponseSuccess(
    public val body: Status,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetStatusResponse() {
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
  public data class GetStatusResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetStatusResponse()

  @Serializable
  public data class GetStatusResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetStatusResponse()

  @Serializable
  public data class GetStatusResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetStatusResponse()

  @Serializable
  public data class GetStatusResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : GetStatusResponse()

  @Serializable
  public data class UpdateStatusRequest(
    public val language: String? = null,
    @SerialName("media_attributes[]")
    public val mediaAttributes: List<String>? = null,
    @SerialName("media_ids")
    public val mediaIds: List<String>? = null,
    public val poll: Poll? = null,
    @SerialName("quote_approval_policy")
    public val quoteApprovalPolicy: String? = null,
    public val sensitive: Boolean? = null,
    @SerialName("spoiler_text")
    public val spoilerText: String? = null,
    public val status: String? = null,
  ) {
    @Serializable
    public data class Poll(
      @SerialName("expires_in")
      public val expiresIn: Long? = null,
      @SerialName("hide_totals")
      public val hideTotals: Boolean? = null,
      public val multiple: Boolean? = null,
      public val options: List<String>? = null,
    )
  }

  @Serializable
  public sealed class UpdateStatusResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class UpdateStatusResponseSuccess(
    public val body: Status,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : UpdateStatusResponse() {
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
  public data class UpdateStatusResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : UpdateStatusResponse()

  @Serializable
  public data class UpdateStatusResponseFailure(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : UpdateStatusResponse()

  @Serializable
  public data class UpdateStatusResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : UpdateStatusResponse()

  @Serializable
  public sealed class DeleteStatusResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class DeleteStatusResponseSuccess(
    public val body: Status,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : DeleteStatusResponse() {
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
  public data class DeleteStatusResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : DeleteStatusResponse()

  @Serializable
  public data class DeleteStatusResponseFailure410(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : DeleteStatusResponse()

  @Serializable
  public data class DeleteStatusResponseFailure(
    public val body: ValidationError,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : DeleteStatusResponse()

  @Serializable
  public data class DeleteStatusResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : DeleteStatusResponse()
}

public fun StatusesClient(configuration: ClientConfiguration = defaultClientConfiguration): StatusesClient = DefaultStatusesClient(configuration)

public class DefaultStatusesClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : StatusesClient {
  override suspend fun getStatuses(id: List<String>?): StatusesClient.GetStatusesResponse {
    try {
      val response = configuration.client.`get`("api/v1/statuses") {
        url {
          if (id != null) {
            parameters.appendAll("id", id)
          }
        }
      }
      return when (response.status.value) {
        200 -> StatusesClient.GetStatusesResponseSuccess(response.body<List<Status>>(), response.headers)
        401, 404, 429, 503 -> StatusesClient.GetStatusesResponseFailure401(response.body<Error>(), response.headers)
        410 -> StatusesClient.GetStatusesResponseFailure410(response.headers)
        422 -> StatusesClient.GetStatusesResponseFailure(response.body<ValidationError>(), response.headers)
        else -> StatusesClient.GetStatusesResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return StatusesClient.GetStatusesResponseUnknownFailure(500)
    }
  }

  override suspend fun createStatus(request: CreateStatusRequest, idempotencyKey: JsonElement?): StatusesClient.CreateStatusResponse {
    try {
      val response = configuration.client.post("api/v1/statuses") {
        if (idempotencyKey != null) {
          setHeader("Idempotency-Key", idempotencyKey)
        }
        setBody(request)
        contentType(ContentType.Application.Json)
      }
      return when (response.status.value) {
        200 -> StatusesClient.CreateStatusResponseSuccess(response.body<CreateStatusResponse>(), response.headers)
        401, 404, 422, 429, 503 -> StatusesClient.CreateStatusResponseFailure401(response.body<Error>(), response.headers)
        410 -> StatusesClient.CreateStatusResponseFailure(response.headers)
        else -> StatusesClient.CreateStatusResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return StatusesClient.CreateStatusResponseUnknownFailure(500)
    }
  }

  override suspend fun getStatus(id: String): StatusesClient.GetStatusResponse {
    try {
      val response = configuration.client.`get`("api/v1/statuses/{id}".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> StatusesClient.GetStatusResponseSuccess(response.body<Status>(), response.headers)
        401, 404, 429, 503 -> StatusesClient.GetStatusResponseFailure401(response.body<Error>(), response.headers)
        410 -> StatusesClient.GetStatusResponseFailure410(response.headers)
        422 -> StatusesClient.GetStatusResponseFailure(response.body<ValidationError>(), response.headers)
        else -> StatusesClient.GetStatusResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return StatusesClient.GetStatusResponseUnknownFailure(500)
    }
  }

  override suspend fun updateStatus(request: StatusesClient.UpdateStatusRequest, id: String): StatusesClient.UpdateStatusResponse {
    try {
      val response = configuration.client.put("api/v1/statuses/{id}".replace("/{id}", "/${id.encodeURLPathPart()}")) {
        setBody(request)
        contentType(ContentType.Application.Json)
      }
      return when (response.status.value) {
        200 -> StatusesClient.UpdateStatusResponseSuccess(response.body<Status>(), response.headers)
        401, 404, 422, 429, 503 -> StatusesClient.UpdateStatusResponseFailure401(response.body<Error>(), response.headers)
        410 -> StatusesClient.UpdateStatusResponseFailure(response.headers)
        else -> StatusesClient.UpdateStatusResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return StatusesClient.UpdateStatusResponseUnknownFailure(500)
    }
  }

  override suspend fun deleteStatus(id: String, deleteMedia: Boolean?): StatusesClient.DeleteStatusResponse {
    try {
      val response = configuration.client.delete("api/v1/statuses/{id}".replace("/{id}", "/${id.encodeURLPathPart()}")) {
        url {
          if (deleteMedia != null) {
            parameters.append("delete_media", deleteMedia.toString())
          }
        }
      }
      return when (response.status.value) {
        200 -> StatusesClient.DeleteStatusResponseSuccess(response.body<Status>(), response.headers)
        401, 404, 429, 503 -> StatusesClient.DeleteStatusResponseFailure401(response.body<Error>(), response.headers)
        410 -> StatusesClient.DeleteStatusResponseFailure410(response.headers)
        422 -> StatusesClient.DeleteStatusResponseFailure(response.body<ValidationError>(), response.headers)
        else -> StatusesClient.DeleteStatusResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return StatusesClient.DeleteStatusResponseUnknownFailure(500)
    }
  }
}
