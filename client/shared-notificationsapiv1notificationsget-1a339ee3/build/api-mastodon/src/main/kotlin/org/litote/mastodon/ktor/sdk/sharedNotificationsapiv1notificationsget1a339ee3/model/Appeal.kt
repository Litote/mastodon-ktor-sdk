package org.litote.mastodon.ktor.sdk.sharedNotificationsapiv1notificationsget1a339ee3.model

import kotlin.String
import kotlinx.serialization.Serializable

@Serializable
public data class Appeal(
  public val state: AppealStateEnum = AppealStateEnum.UNKNOWN_,
  public val text: String,
)
