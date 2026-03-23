package org.litote.mastodon.ktor.sdk.sharedNotificationsapiv2notificationsget01d6a505.model

import kotlin.collections.List
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersgetB7d593a6.model.Account
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget83730355.model.Status

@Serializable
public data class GroupedNotificationsResults(
  public val accounts: List<Account>,
  @SerialName("notification_groups")
  public val notificationGroups: List<NotificationGroup>,
  @SerialName("partial_accounts")
  public val partialAccounts: List<PartialAccountWithAvatar>? = null,
  public val statuses: List<Status>,
)
