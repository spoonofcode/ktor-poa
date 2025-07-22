rootProject.name = "com.spoonofcode.poa.ktor-poa"

dependencyResolutionManagement {
    versionCatalogs {
        create("libraries") {
            from(files("gradle/libs.versions.toml"))
        }
        create("plugins") {
            from(files("gradle/libs.versions.toml"))
        }
    }
}