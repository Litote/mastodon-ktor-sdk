package org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsupdatecredentialspatch02a06ece.model

import kotlin.Boolean
import kotlin.String
import kotlinx.serialization.Serializable

@Serializable
public data class Role(
  public val color: String,
  public val highlighted: Boolean,
  public val id: String,
  public val name: String,
  public val permissions: String,
)
