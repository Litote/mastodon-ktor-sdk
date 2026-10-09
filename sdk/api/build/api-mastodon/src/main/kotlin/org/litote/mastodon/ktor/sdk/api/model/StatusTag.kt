package org.litote.mastodon.ktor.sdk.api.model

import kotlin.String
import kotlinx.serialization.Serializable

@Serializable
public data class StatusTag(
  public val name: String,
  public val url: String,
)
