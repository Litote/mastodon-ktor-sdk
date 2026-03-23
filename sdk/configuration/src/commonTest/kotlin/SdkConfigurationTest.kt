package org.litote.mastodon.ktor.sdk.configuration

import kotlin.test.Test
import kotlin.test.assertEquals

class SdkConfigurationTest {
    @Test
    fun `GIVEN server and token WHEN toClientConfiguration THEN baseUrl uses https`() {
        val config = SdkConfiguration(server = "mastodon.social", token = "mytoken")

        val clientConfig = config.toClientConfiguration()

        assertEquals("https://mastodon.social/", clientConfig.baseUrl)
    }

    @Test
    fun `GIVEN SdkConfiguration defaults WHEN created THEN visibility is unlisted and language is en`() {
        val config = SdkConfiguration(server = "example.com", token = "token")

        assertEquals("unlisted", config.visibility)
        assertEquals("en", config.language)
    }

    @Test
    fun `GIVEN custom visibility and language WHEN SdkConfiguration created THEN values are preserved`() {
        val config = SdkConfiguration(server = "example.com", token = "token", visibility = "public", language = "de")

        assertEquals("public", config.visibility)
        assertEquals("de", config.language)
    }

    @Test
    fun `GIVEN server with subdomain WHEN toClientConfiguration THEN baseUrl preserves subdomain`() {
        val config = SdkConfiguration(server = "fosstodon.org", token = "mytoken")

        val clientConfig = config.toClientConfiguration()

        assertEquals("https://fosstodon.org/", clientConfig.baseUrl)
    }
}
