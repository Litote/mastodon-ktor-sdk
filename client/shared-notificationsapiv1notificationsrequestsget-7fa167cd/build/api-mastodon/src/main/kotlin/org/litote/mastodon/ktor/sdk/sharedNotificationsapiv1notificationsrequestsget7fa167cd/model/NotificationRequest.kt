package org.litote.mastodon.ktor.sdk.sharedNotificationsapiv1notificationsrequestsget7fa167cd.model

import kotlin.String
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersgetB7d593a6.model.Account
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget83730355.model.Status

@Serializable
public data class NotificationRequest(
  public val account: Account,
  @SerialName("created_at")
  public val createdAt: String,
  public val id: String,
  @SerialName("last_status")
  public val lastStatus: Status? = null,
  @SerialName("notifications_count")
  public val notificationsCount: String,
  @SerialName("updated_at")
  public val updatedAt: String,
)
