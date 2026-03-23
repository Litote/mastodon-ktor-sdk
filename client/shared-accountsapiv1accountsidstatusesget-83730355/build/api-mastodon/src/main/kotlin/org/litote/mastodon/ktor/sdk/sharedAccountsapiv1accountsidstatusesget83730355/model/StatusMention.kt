package org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget83730355.model

import kotlin.String
import kotlinx.serialization.Serializable

@Serializable
public data class StatusMention(
  public val acct: String,
  public val id: String,
  public val url: String,
  public val username: String,
)
