pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
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
}

rootProject.name = "Live bus journey tracker"
include(":app")

// Common module
include(":common")

// Core modules
include(":core:common")
include(":core:ui")
include(":core:network")
include(":core:database")
include(":core:domain")
include(":core:data")

// Feature modules
include(":feature:busroutes")
include(":feature:tracking")
include(":feature:home")
include(":feature:tracking:lib")
