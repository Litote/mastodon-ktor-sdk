package org.litote.mastodon.ktor.sdk.api.model

import kotlin.String
import kotlinx.serialization.Serializable

@Serializable
public data class TagHistory(
  public val accounts: String,
  public val day: String,
  public val uses: String,
)
