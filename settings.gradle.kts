pluginManagement {
    resolutionStrategy.eachPlugin {
        if (requested.id.namespace == "org.jetbrains.kotlin") {
            useVersion(embeddedKotlinVersion)
        }
    }
}


rootProject.name = "gradle-testsets-plugin"
