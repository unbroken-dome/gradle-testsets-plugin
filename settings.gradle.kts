val kotlinVersion: String by settings
pluginManagement {
    resolutionStrategy.eachPlugin {
        if (requested.id.namespace == "org.jetbrains.kotlin") {
            useVersion(kotlinVersion)
        }
    }
}


rootProject.name = "gradle-testsets-plugin"
