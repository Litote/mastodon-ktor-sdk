package org.litote.mastodon.ktor.sdk.model

import kotlin.String
import kotlin.collections.List
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsfamiliarfollowersgetB7d593a6.model.Account

@Serializable
public data class FamiliarFollowers(
  public val accounts: List<Account>,
  public val id: String,
)
