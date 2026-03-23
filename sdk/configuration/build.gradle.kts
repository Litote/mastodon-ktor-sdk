plugins {
    id("kotlin-convention")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":client:shared"))
        }
    }
}

mavenPublishing {
    pom {
        description = "Mastodon SDK configuration"
    }
}
