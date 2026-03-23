@file:OptIn(ExperimentalSerializationApi::class)

package org.litote.mastodon.ktor.sdk.model

import kotlin.OptIn
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable

@Serializable
public sealed class CreateStatusRequest
