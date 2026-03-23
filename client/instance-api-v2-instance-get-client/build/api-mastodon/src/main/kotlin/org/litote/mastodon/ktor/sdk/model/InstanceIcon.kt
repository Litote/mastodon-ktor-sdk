package org.litote.mastodon.ktor.sdk.model

import kotlin.String
import kotlinx.serialization.Serializable

@Serializable
public data class InstanceIcon(
  public val size: String,
  public val src: String,
)
