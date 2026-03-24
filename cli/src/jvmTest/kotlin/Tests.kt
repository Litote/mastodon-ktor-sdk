package org.litote.mastodon.ktor.sdk.send

import java.io.ByteArrayOutputStream
import java.io.PrintStream

internal suspend fun captureStdout(block: suspend () -> Unit): String {
    val out = ByteArrayOutputStream()
    val original = System.out
    System.setOut(PrintStream(out))
    try {
        block()
    } finally {
        System.setOut(original)
    }
    return out.toString()
}

internal suspend fun captureStderr(block: suspend () -> Unit): String {
    val err = ByteArrayOutputStream()
    val original = System.err
    System.setErr(PrintStream(err))
    try {
        block()
    } finally {
        System.setErr(original)
    }
    return err.toString()
}
