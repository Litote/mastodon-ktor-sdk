package org.litote.mastodon.ktor.sdk.cli

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ParseArgsTest {
    @Test
    fun `GIVEN valid server and token WHEN parseArgs THEN returns correct config`() {
        val (config, remaining) = parseArgs(arrayOf("--server", "mastodon.social", "--token", "abc123"))

        assertEquals("mastodon.social", config.server)
        assertEquals("abc123", config.token)
        assertEquals("unlisted", config.visibility)
        assertEquals("en", config.language)
        assertEquals(emptyList(), remaining)
    }

    @Test
    fun `GIVEN missing server WHEN parseArgs THEN throws`() {
        assertFailsWith<IllegalArgumentException> {
            parseArgs(arrayOf("--token", "abc123"))
        }
    }

    @Test
    fun `GIVEN missing token WHEN parseArgs THEN throws`() {
        assertFailsWith<IllegalArgumentException> {
            parseArgs(arrayOf("--server", "mastodon.social"))
        }
    }

    @Test
    fun `GIVEN custom visibility and language WHEN parseArgs THEN uses provided values`() {
        val (config, _) =
            parseArgs(
                arrayOf("--server", "mastodon.social", "--token", "abc", "--visibility", "public", "--language", "fr"),
            )

        assertEquals("public", config.visibility)
        assertEquals("fr", config.language)
    }

    @Test
    fun `GIVEN positional args mixed with flags WHEN parseArgs THEN returns remaining args`() {
        val (_, remaining) =
            parseArgs(
                arrayOf("--server", "mastodon.social", "--token", "abc", "hello", "world"),
            )

        assertEquals(listOf("hello", "world"), remaining)
    }

    @Test
    fun `GIVEN positional args before flags WHEN parseArgs THEN returns them as remaining`() {
        val (_, remaining) =
            parseArgs(
                arrayOf("hello world", "--server", "mastodon.social", "--token", "abc"),
            )

        assertEquals(listOf("hello world"), remaining)
    }

    @Test
    fun `GIVEN --simulate flag WHEN parseArgs THEN simulate is true`() {
        val parsed = parseArgs(arrayOf("--server", "mastodon.social", "--token", "abc", "--simulate"))

        assertEquals(true, parsed.simulate)
    }

    @Test
    fun `GIVEN no --simulate flag WHEN parseArgs THEN simulate is false`() {
        val parsed = parseArgs(arrayOf("--server", "mastodon.social", "--token", "abc"))

        assertEquals(false, parsed.simulate)
    }

    @Test
    fun `GIVEN --simulate flag mixed with positional args WHEN parseArgs THEN simulate is true and remaining is correct`() {
        val parsed =
            parseArgs(
                arrayOf("--server", "mastodon.social", "--token", "abc", "--simulate", "hello", "world"),
            )

        assertEquals(true, parsed.simulate)
        assertEquals(listOf("hello", "world"), parsed.remaining)
    }
}
