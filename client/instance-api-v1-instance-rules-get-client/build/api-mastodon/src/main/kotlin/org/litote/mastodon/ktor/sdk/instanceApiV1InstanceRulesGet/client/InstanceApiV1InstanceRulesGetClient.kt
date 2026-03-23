package org.litote.mastodon.ktor.sdk.instanceApiV1InstanceRulesGet.client

import io.ktor.client.call.body
import io.ktor.client.request.`get`
import kotlin.Int
import kotlin.collections.List
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration.Companion.defaultClientConfiguration
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersget162656ba.model.Error
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsgetF0eb0b5e.model.ValidationError
import org.litote.mastodon.ktor.sdk.sharedInstanceapiv1instanceget2050c4cc.model.Rule

public class InstanceApiV1InstanceRulesGetClient(
  private val configuration: ClientConfiguration = defaultClientConfiguration,
) {
  /**
   * List of rules
   */
  public suspend fun getInstanceRules(): GetInstanceRulesResponse {
    try {
      val response = configuration.client.`get`("api/v1/instance/rules") {
      }
      return when (response.status.value) {
        200 -> GetInstanceRulesResponseSuccess(response.body<List<Rule>>())
        401, 404, 429, 503 -> GetInstanceRulesResponseFailure401(response.body<Error>())
        410 -> GetInstanceRulesResponseFailure410
        422 -> GetInstanceRulesResponseFailure(response.body<ValidationError>())
        else -> GetInstanceRulesResponseUnknownFailure(response.status.value)
      }
    }
    catch(e: Exception) {
      configuration.exceptionLogger(e)
      return GetInstanceRulesResponseUnknownFailure(500)
    }
  }

  @Serializable
  public sealed class GetInstanceRulesResponse

  @Serializable
  public data class GetInstanceRulesResponseSuccess(
    public val body: List<Rule>,
  ) : GetInstanceRulesResponse()

  @Serializable
  public data class GetInstanceRulesResponseFailure401(
    public val body: Error,
  ) : GetInstanceRulesResponse()

  @Serializable
  public object GetInstanceRulesResponseFailure410 : GetInstanceRulesResponse()

  @Serializable
  public data class GetInstanceRulesResponseFailure(
    public val body: ValidationError,
  ) : GetInstanceRulesResponse()

  @Serializable
  public data class GetInstanceRulesResponseUnknownFailure(
    public val statusCode: Int,
  ) : GetInstanceRulesResponse()
}
