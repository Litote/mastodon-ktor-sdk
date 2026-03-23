package org.litote.mastodon.ktor.sdk.model

import kotlin.Boolean
import kotlinx.serialization.Serializable

@Serializable
public data class MergedResponse(
  public val merged: Boolean,
)
