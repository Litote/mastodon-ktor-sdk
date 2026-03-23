package org.litote.mastodon.ktor.sdk.configuration

import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration

public data class SdkConfiguration(
    val server: String,
    val token: String,
    val visibility: String = "unlisted",
    val language: String = "en",
)

public fun SdkConfiguration.toClientConfiguration(): ClientConfiguration {
    val json =
        Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        }
    val baseUrl = "https://$server/"
    return ClientConfiguration(
        baseUrl = baseUrl,
        json = json,
        httpClientConfig = {
            install(Logging)
            install(ContentNegotiation) { json(json) }
            defaultRequest {
                url(baseUrl)
                header("Authorization", "Bearer $token")
            }
        },
    )
}
