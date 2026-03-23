package org.litote.mastodon.ktor.sdk.filtersApiV1FiltersIdDelete.client

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

public class FiltersApiV1FiltersIdDeleteClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Remove a filter
   */
  public suspend fun deleteFilter(id: String): DeleteFilterResponse {
    try {
      val response = configuration.client.delete("api/v1/filters/{id}".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> DeleteFilterResponseSuccess
        401, 404, 429, 503 -> DeleteFilterResponseFailure401(response.body<Error>())
        410 -> DeleteFilterResponseFailure410
        422 -> DeleteFilterResponseFailure(response.body<ValidationError>())
        else -> DeleteFilterResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return DeleteFilterResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class DeleteFilterResponse

  @Serializable
  public object DeleteFilterResponseSuccess : DeleteFilterResponse()

  @Serializable
  public data class DeleteFilterResponseFailure401(
    public val body: Error,
  ) : DeleteFilterResponse()

  @Serializable
  public object DeleteFilterResponseFailure410 : DeleteFilterResponse()

  @Serializable
  public data class DeleteFilterResponseFailure(
    public val body: ValidationError,
  ) : DeleteFilterResponse()

  @Serializable
  public data class DeleteFilterResponseUnknownFailure(
    public val statusCode: Int,
  ) : DeleteFilterResponse()
}
