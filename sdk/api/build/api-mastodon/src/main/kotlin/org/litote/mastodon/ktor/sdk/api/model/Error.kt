package org.litote.mastodon.ktor.sdk.api.model

import kotlin.String
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class Error(
  public val error: String,
  @SerialName("error_description")
  public val errorDescription: String? = null,
)
