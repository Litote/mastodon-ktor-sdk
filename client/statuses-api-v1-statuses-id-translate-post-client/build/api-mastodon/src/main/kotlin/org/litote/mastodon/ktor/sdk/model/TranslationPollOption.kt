package org.litote.mastodon.ktor.sdk.model

import kotlin.String
import kotlinx.serialization.Serializable

@Serializable
public data class TranslationPollOption(
  public val title: String,
)
