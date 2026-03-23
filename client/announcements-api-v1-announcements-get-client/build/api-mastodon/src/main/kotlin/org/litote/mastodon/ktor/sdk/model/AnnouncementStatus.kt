package org.litote.mastodon.ktor.sdk.model

import kotlin.String
import kotlinx.serialization.Serializable

@Serializable
public data class AnnouncementStatus(
  public val id: String,
  public val url: String,
)
