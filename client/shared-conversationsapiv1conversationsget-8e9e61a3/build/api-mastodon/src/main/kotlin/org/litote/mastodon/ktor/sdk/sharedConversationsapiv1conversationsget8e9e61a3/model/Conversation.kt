package org.litote.mastodon.ktor.sdk.sharedConversationsapiv1conversationsget8e9e61a3.model

import kotlin.Boolean
import kotlin.String
import kotlin.collections.List
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersgetB7d593a6.model.Account
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget83730355.model.Status

@Serializable
public data class Conversation(
  public val accounts: List<Account>,
  public val id: String,
  @SerialName("last_status")
  public val lastStatus: Status? = null,
  public val unread: Boolean,
)
