package org.litote.mastodon.ktor.sdk.sharedNotificationsapiv1notificationsget1a339ee3.model

import kotlin.String
import kotlin.collections.List
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersgetB7d593a6.model.Account

@Serializable
public data class AccountWarning(
  public val action: AccountWarningActionEnum = AccountWarningActionEnum.UNKNOWN_,
  public val appeal: Appeal? = null,
  @SerialName("created_at")
  public val createdAt: String,
  public val id: String,
  @SerialName("status_ids")
  public val statusIds: List<String>? = null,
  @SerialName("target_account")
  public val targetAccount: Account,
  public val text: String,
)
