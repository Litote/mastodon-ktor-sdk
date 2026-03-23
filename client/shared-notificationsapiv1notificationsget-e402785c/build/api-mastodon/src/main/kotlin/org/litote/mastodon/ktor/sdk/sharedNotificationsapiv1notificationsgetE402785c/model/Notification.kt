package org.litote.mastodon.ktor.sdk.sharedNotificationsapiv1notificationsgetE402785c.model

import kotlin.String
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersgetB7d593a6.model.Account
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget83730355.model.Status
import org.litote.mastodon.ktor.sdk.sharedNotificationsapiv1notificationsget1a339ee3.model.AccountWarning
import org.litote.mastodon.ktor.sdk.sharedNotificationsapiv1notificationsget1a339ee3.model.NotificationTypeEnum
import org.litote.mastodon.ktor.sdk.sharedNotificationsapiv1notificationsget1a339ee3.model.RelationshipSeveranceEvent
import org.litote.mastodon.ktor.sdk.sharedNotificationsapiv1notificationsget35be95f4.model.Report

@Serializable
public data class Notification(
  public val account: Account,
  @SerialName("created_at")
  public val createdAt: String,
  public val event: RelationshipSeveranceEvent? = null,
  @SerialName("group_key")
  public val groupKey: String? = null,
  public val id: String,
  @SerialName("moderation_warning")
  public val moderationWarning: AccountWarning? = null,
  public val report: Report? = null,
  public val status: Status? = null,
  public val type: NotificationTypeEnum = NotificationTypeEnum.UNKNOWN_,
)
