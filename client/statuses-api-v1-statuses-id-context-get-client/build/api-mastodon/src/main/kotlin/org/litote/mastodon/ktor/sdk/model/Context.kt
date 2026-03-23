package org.litote.mastodon.ktor.sdk.model

import kotlin.collections.List
import kotlinx.serialization.Serializable
import org.litote.mastodon.ktor.sdk.sharedAccountsapiv1accountsidstatusesget83730355.model.Status

@Serializable
public data class Context(
  public val ancestors: List<Status>,
  public val descendants: List<Status>,
)
