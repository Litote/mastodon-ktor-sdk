package org.litote.mastodon.ktor.sdk.sharedNotificationsapiv1notificationsget35be95f4.model

import kotlin.Boolean
import kotlin.String
import kotlin.collections.List
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersgetB7d593a6.model.Account

@Serializable
public data class Report(
  @SerialName("action_taken")
  public val actionTaken: Boolean,
  @SerialName("action_taken_at")
  public val actionTakenAt: String? = null,
  public val category: ReportCategoryEnum = ReportCategoryEnum.UNKNOWN_,
  public val comment: String,
  @SerialName("created_at")
  public val createdAt: String,
  public val forwarded: Boolean,
  public val id: String,
  @SerialName("rule_ids")
  public val ruleIds: List<String>? = null,
  @SerialName("status_ids")
  public val statusIds: List<String>? = null,
  @SerialName("target_account")
  public val targetAccount: Account,
)
