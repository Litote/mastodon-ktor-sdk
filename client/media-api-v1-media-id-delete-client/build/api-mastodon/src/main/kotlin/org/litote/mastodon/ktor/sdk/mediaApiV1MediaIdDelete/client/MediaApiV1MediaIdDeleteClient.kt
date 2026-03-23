package org.litote.mastodon.ktor.sdk.mediaApiV1MediaIdDelete.client

import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.http.encodeURLPathPart
import kotlin.Int
import kotlin.String
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError

public class MediaApiV1MediaIdDeleteClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Delete media attachment
   */
  public suspend fun deleteMedia(id: String): DeleteMediaResponse {
    try {
      val response = configuration.client.delete("api/v1/media/{id}".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> DeleteMediaResponseSuccess
        401, 404, 429, 503 -> DeleteMediaResponseFailure401(response.body<Error>())
        410 -> DeleteMediaResponseFailure410
        422 -> DeleteMediaResponseFailure(response.body<ValidationError>())
        else -> DeleteMediaResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return DeleteMediaResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class DeleteMediaResponse

  @Serializable
  public object DeleteMediaResponseSuccess : DeleteMediaResponse()

  @Serializable
  public data class DeleteMediaResponseFailure401(
    public val body: Error,
  ) : DeleteMediaResponse()

  @Serializable
  public object DeleteMediaResponseFailure410 : DeleteMediaResponse()

  @Serializable
  public data class DeleteMediaResponseFailure(
    public val body: ValidationError,
  ) : DeleteMediaResponse()

  @Serializable
  public data class DeleteMediaResponseUnknownFailure(
    public val statusCode: Int,
  ) : DeleteMediaResponse()
}
