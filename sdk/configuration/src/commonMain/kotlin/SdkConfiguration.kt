package org.litote.mastodon.ktor.sdk.configuration

import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.litote.mastodon.ktor.sdk.client.ClientConfiguration

/**
 * Configuration required to connect to a Mastodon server and post statuses.
 *
 * @property server Hostname of the Mastodon instance (e.g. `mastodon.social`). Must not include the scheme.
 * @property token OAuth2 bearer token used to authenticate API requests.
 * @property visibility Default visibility for posted statuses. Accepted values: `public`, `unlisted`, `private`, `direct`.
 * @property language BCP 47 language tag applied to posted statuses (e.g. `en`, `fr`).
 */
public data class SdkConfiguration(
    val server: String,
    val token: String,
    val visibility: String = "unlisted",
    val language: String = "en",
)

/**
 * Converts this [SdkConfiguration] into a low-level [ClientConfiguration] suitable for the
 * generated Ktor API clients.
 *
 * The resulting configuration:
 * - Sets the base URL to `https://<server>/`
 * - Installs JSON content negotiation with lenient deserialization (unknown keys ignored, input values coerced)
 * - Adds an `Authorization: Bearer <token>` header to every request
 * - Enables Ktor [Logging]
 */
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
