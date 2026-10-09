plugins {
    id("kotlin-convention")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            // ClientConfiguration is part of the public API (toClientConfiguration()).
            api(project(":sdk:api"))
        }
    }
}

mavenPublishing {
    pom {
        description = "Mastodon SDK configuration"
    }
}
