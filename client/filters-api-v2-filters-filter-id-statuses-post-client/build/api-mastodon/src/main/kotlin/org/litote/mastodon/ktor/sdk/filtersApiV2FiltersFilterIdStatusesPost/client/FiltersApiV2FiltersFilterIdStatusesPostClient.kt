package org.litote.mastodon.ktor.sdk.filtersApiV2FiltersFilterIdStatusesPost.client

import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.encodeURLPathPart
import kotlin.Int
import kotlin.String
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget32903fc7.model.FilterStatus

public class FiltersApiV2FiltersFilterIdStatusesPostClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Add a status to a filter group
   */
  public suspend fun postFilterStatusesV2(request: PostFilterStatusesV2Request, filterId: String): PostFilterStatusesV2Response {
    try {
      val response = configuration.client.post("api/v2/filters/{filter_id}/statuses".replace("/{filter_id}", "/${filterId.encodeURLPathPart()}")) {
        setBody(request)
        contentType(ContentType.Application.Json)
      }
      return when (response.status.value) {
        200 -> PostFilterStatusesV2ResponseSuccess(response.body<FilterStatus>())
        401, 404, 429, 503 -> PostFilterStatusesV2ResponseFailure401(response.body<Error>())
        410 -> PostFilterStatusesV2ResponseFailure410
        422 -> PostFilterStatusesV2ResponseFailure(response.body<ValidationError>())
        else -> PostFilterStatusesV2ResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return PostFilterStatusesV2ResponseUnknownFailure(500)
    }
  }

  @Serializable
  public data class PostFilterStatusesV2Request(
    @SerialName("status_id")
    public val statusId: String,
  )

  @Serializable
  public sealed class PostFilterStatusesV2Response

  @Serializable
  public data class PostFilterStatusesV2ResponseSuccess(
    public val body: FilterStatus,
  ) : PostFilterStatusesV2Response()

  @Serializable
  public data class PostFilterStatusesV2ResponseFailure401(
    public val body: Error,
  ) : PostFilterStatusesV2Response()

  @Serializable
  public object PostFilterStatusesV2ResponseFailure410 : PostFilterStatusesV2Response()

  @Serializable
  public data class PostFilterStatusesV2ResponseFailure(
    public val body: ValidationError,
  ) : PostFilterStatusesV2Response()

  @Serializable
  public data class PostFilterStatusesV2ResponseUnknownFailure(
    public val statusCode: Int,
  ) : PostFilterStatusesV2Response()
}
