plugins {
    id("signing")
}

signing {
    val inMemoryKey = providers.gradleProperty("signingInMemoryKey").orNull
    if (inMemoryKey != null) {
        val keyId = providers.gradleProperty("signingInMemoryKeyId").orNull
        val password = providers.gradleProperty("signingInMemoryKeyPassword").orNull ?: ""
        useInMemoryPgpKeys(keyId, inMemoryKey, password)
    }
    isRequired = inMemoryKey != null
}


// For KMP projects, JaCoCo creates jacocoJvmTestReport (not jacocoTestReport)
tasks.withType<JacocoReport>().configureEach {
    reports {
        xml.required.set(true)
        html.required.set(false)
    }
}
