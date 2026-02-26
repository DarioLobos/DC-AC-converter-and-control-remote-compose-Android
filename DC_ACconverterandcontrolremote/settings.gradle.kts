pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
    // DELETE THE versionCatalogs BLOCK FROM HERE
}

rootProject.name = "DC_ACconverterandcontrolremote"
include(":app")
include(":shared")
