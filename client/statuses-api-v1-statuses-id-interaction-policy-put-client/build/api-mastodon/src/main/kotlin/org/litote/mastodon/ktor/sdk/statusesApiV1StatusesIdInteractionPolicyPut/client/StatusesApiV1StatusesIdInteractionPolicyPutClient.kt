package org.litote.mastodon.ktor.sdk.statusesApiV1StatusesIdInteractionPolicyPut.client

import io.ktor.client.call.body
import io.ktor.client.request.put
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
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget83730355.model.Status

public class StatusesApiV1StatusesIdInteractionPolicyPutClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * Edit a status' interaction policies
   */
  public suspend fun updateStatusInteractionPolicy(request: UpdateStatusInteractionPolicyRequest, id: String): UpdateStatusInteractionPolicyResponse {
    try {
      val response = configuration.client.put("api/v1/statuses/{id}/interaction_policy".replace("/{id}", "/${id.encodeURLPathPart()}")) {
        setBody(request)
        contentType(ContentType.Application.Json)
      }
      return when (response.status.value) {
        200 -> UpdateStatusInteractionPolicyResponseSuccess(response.body<Status>())
        401, 404, 429, 503 -> UpdateStatusInteractionPolicyResponseFailure401(response.body<Error>())
        410 -> UpdateStatusInteractionPolicyResponseFailure410
        422 -> UpdateStatusInteractionPolicyResponseFailure(response.body<ValidationError>())
        else -> UpdateStatusInteractionPolicyResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return UpdateStatusInteractionPolicyResponseUnknownFailure(500)
    }
  }

  @Serializable
  public data class UpdateStatusInteractionPolicyRequest(
    @SerialName("quote_approval_policy")
    public val quoteApprovalPolicy: String? = null,
  )

  @Serializable
  public sealed class UpdateStatusInteractionPolicyResponse

  @Serializable
  public data class UpdateStatusInteractionPolicyResponseSuccess(
    public val body: Status,
  ) : UpdateStatusInteractionPolicyResponse()

  @Serializable
  public data class UpdateStatusInteractionPolicyResponseFailure401(
    public val body: Error,
  ) : UpdateStatusInteractionPolicyResponse()

  @Serializable
  public object UpdateStatusInteractionPolicyResponseFailure410 : UpdateStatusInteractionPolicyResponse()

  @Serializable
  public data class UpdateStatusInteractionPolicyResponseFailure(
    public val body: ValidationError,
  ) : UpdateStatusInteractionPolicyResponse()

  @Serializable
  public data class UpdateStatusInteractionPolicyResponseUnknownFailure(
    public val statusCode: Int,
  ) : UpdateStatusInteractionPolicyResponse()
}
