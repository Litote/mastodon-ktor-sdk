package org.litote.mastodon.ktor.sdk.statusesApiV1StatusesIdDelete.client

import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.http.encodeURLPathPart
import kotlin.Boolean
import kotlin.Int
import kotlin.String
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget83730355.model.Status

public class StatusesApiV1StatusesIdDeleteClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Delete a status
   */
  public suspend fun deleteStatus(id: String, deleteMedia: Boolean? = null): DeleteStatusResponse {
    try {
      val response = configuration.client.delete("api/v1/statuses/{id}".replace("/{id}", "/${id.encodeURLPathPart()}")) {
        url {
          if (deleteMedia != null) {
            parameters.append("delete_media", deleteMedia.toString())
          }
        }
      }
      return when (response.status.value) {
        200 -> DeleteStatusResponseSuccess(response.body<Status>())
        401, 404, 429, 503 -> DeleteStatusResponseFailure401(response.body<Error>())
        410 -> DeleteStatusResponseFailure410
        422 -> DeleteStatusResponseFailure(response.body<ValidationError>())
        else -> DeleteStatusResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return DeleteStatusResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class DeleteStatusResponse

  @Serializable
  public data class DeleteStatusResponseSuccess(
    public val body: Status,
  ) : DeleteStatusResponse()

  @Serializable
  public data class DeleteStatusResponseFailure401(
    public val body: Error,
  ) : DeleteStatusResponse()

  @Serializable
  public object DeleteStatusResponseFailure410 : DeleteStatusResponse()

  @Serializable
  public data class DeleteStatusResponseFailure(
    public val body: ValidationError,
  ) : DeleteStatusResponse()

  @Serializable
  public data class DeleteStatusResponseUnknownFailure(
    public val statusCode: Int,
  ) : DeleteStatusResponse()
}
