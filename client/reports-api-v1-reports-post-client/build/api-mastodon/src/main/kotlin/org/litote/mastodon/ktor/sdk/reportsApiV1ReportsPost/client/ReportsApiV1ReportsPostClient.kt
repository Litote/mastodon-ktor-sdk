package org.litote.mastodon.ktor.sdk.reportsApiV1ReportsPost.client

import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.contentType
import kotlin.Boolean
import kotlin.Int
import kotlin.String
import kotlin.collections.List
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedNotificationsapiv1notificationsget35be95f4.model.Report
import org.litote.mastodon.ktor.sdk.sharedNotificationsapiv1notificationsget35be95f4.model.ReportCategoryEnum

public interface ReportsApiV1ReportsPostClient {
  /**
   * File a report
   */
  public suspend fun createReport(request: CreateReportRequest): CreateReportResponse

  @Serializable
  public data class CreateReportRequest(
    @SerialName("account_id")
    public val accountId: String,
    public val category: ReportCategoryEnum? = null,
    public val comment: String? = null,
    public val forward: Boolean? = false,
    @SerialName("rule_ids")
    public val ruleIds: List<String>? = null,
    @SerialName("status_ids")
    public val statusIds: List<String>? = null,
  )

  @Serializable
  public sealed class CreateReportResponse {
    public abstract val headers: Headers
  }

  @Serializable
  public data class CreateReportResponseSuccess(
    public val body: Report,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : CreateReportResponse() {
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
  public data class CreateReportResponseFailure401(
    public val body: Error,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : CreateReportResponse()

  @Serializable
  public data class CreateReportResponseFailure(
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : CreateReportResponse()

  @Serializable
  public data class CreateReportResponseUnknownFailure(
    public val statusCode: Int,
    @Transient
    override val headers: Headers = Headers.Empty,
  ) : CreateReportResponse()
}

public fun ReportsApiV1ReportsPostClient(configuration: ClientConfiguration = defaultClientConfiguration): ReportsApiV1ReportsPostClient = DefaultReportsApiV1ReportsPostClient(configuration)

public class DefaultReportsApiV1ReportsPostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) : ReportsApiV1ReportsPostClient {
  override suspend fun createReport(request: ReportsApiV1ReportsPostClient.CreateReportRequest): ReportsApiV1ReportsPostClient.CreateReportResponse {
    try {
      val response = configuration.client.post("api/v1/reports") {
        setBody(request)
        contentType(ContentType.Application.Json)
      }
      return when (response.status.value) {
        200 -> ReportsApiV1ReportsPostClient.CreateReportResponseSuccess(response.body<Report>(), response.headers)
        401, 404, 422, 429, 503 -> ReportsApiV1ReportsPostClient.CreateReportResponseFailure401(response.body<Error>(), response.headers)
        410 -> ReportsApiV1ReportsPostClient.CreateReportResponseFailure(response.headers)
        else -> ReportsApiV1ReportsPostClient.CreateReportResponseUnknownFailure(response.status.value, response.headers)
      }
    }
    catch(e: CancellationException) {
      throw e
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return ReportsApiV1ReportsPostClient.CreateReportResponseUnknownFailure(500)
    }
  }
}
