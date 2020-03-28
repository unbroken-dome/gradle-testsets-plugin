pluginManagement {
    resolutionStrategy.eachPlugin {
        if (requested.id.namespace == "org.jetbrains.kotlin") {
            val kotlinVersion: String by settings
            useVersion(kotlinVersion)
        }
    }
}


rootProject.name = "gradle-testsets-plugin"
