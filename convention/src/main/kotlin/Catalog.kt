import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

fun Project.lib(name: String) =
    extensions
        .getByType(VersionCatalogsExtension::class)
        .named("openapi")
        .findLibrary(name)
        .orElseThrow { RuntimeException("lib not found in catalog : $name") }

fun Project.bundle(name: String) =
    extensions
        .getByType(VersionCatalogsExtension::class)
        .named("openapi")
        .findBundle(name)
        .orElseThrow { RuntimeException("bundle not found in catalog : $name") }

fun Project.plugin(alias: String) {
    val catalogs = extensions.getByType<VersionCatalogsExtension>()
    val libs = catalogs.named("openapi")
    this.pluginManager.apply(
        libs
            .findPlugin(alias)
            .get()
            .get()
            .pluginId,
    )
}