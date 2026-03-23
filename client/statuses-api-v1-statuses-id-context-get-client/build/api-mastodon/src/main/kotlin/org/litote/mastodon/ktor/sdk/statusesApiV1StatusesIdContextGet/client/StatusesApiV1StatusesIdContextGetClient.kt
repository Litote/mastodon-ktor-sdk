package org.litote.mastodon.ktor.sdk.statusesApiV1StatusesIdContextGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import io.ktor.http.encodeURLPathPart
import kotlin.Int
import kotlin.String
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.model.Context
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError

public class StatusesApiV1StatusesIdContextGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Get parent and child statuses in context
   */
  public suspend fun getStatusContext(id: String): GetStatusContextResponse {
    try {
      val response = configuration.client.`get`("api/v1/statuses/{id}/context".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> GetStatusContextResponseSuccess(response.body<Context>())
        401, 404, 429, 503 -> GetStatusContextResponseFailure401(response.body<Error>())
        410 -> GetStatusContextResponseFailure410
        422 -> GetStatusContextResponseFailure(response.body<ValidationError>())
        else -> GetStatusContextResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return GetStatusContextResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class GetStatusContextResponse

  @Serializable
  public data class GetStatusContextResponseSuccess(
    public val body: Context,
  ) : GetStatusContextResponse()

  @Serializable
  public data class GetStatusContextResponseFailure401(
    public val body: Error,
  ) : GetStatusContextResponse()

  @Serializable
  public object GetStatusContextResponseFailure410 : GetStatusContextResponse()

  @Serializable
  public data class GetStatusContextResponseFailure(
    public val body: ValidationError,
  ) : GetStatusContextResponse()

  @Serializable
  public data class GetStatusContextResponseUnknownFailure(
    public val statusCode: Int,
  ) : GetStatusContextResponse()
}
