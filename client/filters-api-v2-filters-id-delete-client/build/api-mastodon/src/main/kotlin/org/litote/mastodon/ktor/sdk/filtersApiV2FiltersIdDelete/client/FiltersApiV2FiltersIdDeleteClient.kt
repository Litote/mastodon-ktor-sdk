package org.litote.mastodon.ktor.sdk.filtersApiV2FiltersIdDelete.client

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

public class FiltersApiV2FiltersIdDeleteClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Delete a filter
   */
  public suspend fun deleteFilterV2(id: String): DeleteFilterV2Response {
    try {
      val response = configuration.client.delete("api/v2/filters/{id}".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> DeleteFilterV2ResponseSuccess
        401, 404, 429, 503 -> DeleteFilterV2ResponseFailure401(response.body<Error>())
        410 -> DeleteFilterV2ResponseFailure410
        422 -> DeleteFilterV2ResponseFailure(response.body<ValidationError>())
        else -> DeleteFilterV2ResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return DeleteFilterV2ResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class DeleteFilterV2Response

  @Serializable
  public object DeleteFilterV2ResponseSuccess : DeleteFilterV2Response()

  @Serializable
  public data class DeleteFilterV2ResponseFailure401(
    public val body: Error,
  ) : DeleteFilterV2Response()

  @Serializable
  public object DeleteFilterV2ResponseFailure410 : DeleteFilterV2Response()

  @Serializable
  public data class DeleteFilterV2ResponseFailure(
    public val body: ValidationError,
  ) : DeleteFilterV2Response()

  @Serializable
  public data class DeleteFilterV2ResponseUnknownFailure(
    public val statusCode: Int,
  ) : DeleteFilterV2Response()
}
