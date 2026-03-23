package org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget2b22751c.model

import kotlin.String
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersgetB7d593a6.model.Account

@Serializable
public data class PreviewCardAuthor(
  public val account: Account? = null,
  public val name: String,
  public val url: String,
)
