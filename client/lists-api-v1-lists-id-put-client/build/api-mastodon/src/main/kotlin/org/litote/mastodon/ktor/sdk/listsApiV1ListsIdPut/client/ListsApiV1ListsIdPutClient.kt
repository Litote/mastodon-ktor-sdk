package org.litote.mastodon.ktor.sdk.listsApiV1ListsIdPut.client

import io.ktor.client.call.body
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.encodeURLPathPart
import kotlin.Boolean
import kotlin.Int
import kotlin.String
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidlistsgetDaf64318.model.List
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidlistsgetDaf64318.model.ListRepliesPolicyEnum

public class ListsApiV1ListsIdPutClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Update a list
   */
  public suspend fun updateList(request: UpdateListRequest, id: String): UpdateListResponse {
    try {
      val response = configuration.client.put("api/v1/lists/{id}".replace("/{id}", "/${id.encodeURLPathPart()}")) {
        setBody(request)
        contentType(ContentType.Application.Json)
      }
      return when (response.status.value) {
        200 -> UpdateListResponseSuccess(response.body<List>())
        401, 404, 422, 429, 503 -> UpdateListResponseFailure401(response.body<Error>())
        410 -> UpdateListResponseFailure
        else -> UpdateListResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return UpdateListResponseUnknownFailure(500)
    }
  }

  @Serializable
  public data class UpdateListRequest(
    public val exclusive: Boolean? = null,
    @SerialName("replies_policy")
    public val repliesPolicy: ListRepliesPolicyEnum? = null,
    public val title: String,
  )

  @Serializable
  public sealed class UpdateListResponse

  @Serializable
  public data class UpdateListResponseSuccess(
    public val body: List,
  ) : UpdateListResponse()

  @Serializable
  public data class UpdateListResponseFailure401(
    public val body: Error,
  ) : UpdateListResponse()

  @Serializable
  public object UpdateListResponseFailure : UpdateListResponse()

  @Serializable
  public data class UpdateListResponseUnknownFailure(
    public val statusCode: Int,
  ) : UpdateListResponse()
}
