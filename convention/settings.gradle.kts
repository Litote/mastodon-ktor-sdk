rootProject.name = "convention"


val generatorVersion = run {
    val toml = file("../gradle/libs.versions.toml").readText()
    Regex("""generator\s*=\s*"([^"]+)"""").find(toml)?.groupValues[1] ?: error("generator version not found")
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
        create("openapi") {
            from("org.litote.openapi.ktor.client.generator:version-catalog:$generatorVersion")
        }
    }
}
