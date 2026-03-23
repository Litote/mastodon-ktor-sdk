package org.litote.mastodon.ktor.sdk.listsApiV1ListsIdDelete.client

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

public class ListsApiV1ListsIdDeleteClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Delete a list
   */
  public suspend fun deleteList(id: String): DeleteListResponse {
    try {
      val response = configuration.client.delete("api/v1/lists/{id}".replace("/{id}", "/${id.encodeURLPathPart()}")) {
      }
      return when (response.status.value) {
        200 -> DeleteListResponseSuccess
        401, 404, 429, 503 -> DeleteListResponseFailure401(response.body<Error>())
        410 -> DeleteListResponseFailure410
        422 -> DeleteListResponseFailure(response.body<ValidationError>())
        else -> DeleteListResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return DeleteListResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class DeleteListResponse

  @Serializable
  public object DeleteListResponseSuccess : DeleteListResponse()

  @Serializable
  public data class DeleteListResponseFailure401(
    public val body: Error,
  ) : DeleteListResponse()

  @Serializable
  public object DeleteListResponseFailure410 : DeleteListResponse()

  @Serializable
  public data class DeleteListResponseFailure(
    public val body: ValidationError,
  ) : DeleteListResponse()

  @Serializable
  public data class DeleteListResponseUnknownFailure(
    public val statusCode: Int,
  ) : DeleteListResponse()
}
