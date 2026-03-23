package org.litote.mastodon.ktor.sdk.model

import kotlin.String
import kotlinx.serialization.Serializable

@Serializable
public data class TranslationAttachment(
  public val description: String,
  public val id: String,
)
