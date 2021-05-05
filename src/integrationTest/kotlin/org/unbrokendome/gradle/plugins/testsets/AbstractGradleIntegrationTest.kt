package org.unbrokendome.gradle.plugins.testsets

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import java.io.File
import java.nio.file.Files


abstract class AbstractGradleIntegrationTest {

    protected lateinit var projectDir: File


    @BeforeEach
    fun setupProject() {
        projectDir = Files.createTempDirectory("gradle").toFile()
        val projectName = projectDir.name

        // Always create a settings file, otherwise Gradle searches up the directory hierarchy
        // (and might actually find another file)
        directory(projectDir) {
            file(
                "settings.gradle",
                contents = """ 
                rootProject.name = '$projectName'
                """
            )
        }
    }


    @AfterEach
    fun cleanupProject() {
        projectDir.deleteRecursively()
    }


    protected fun runGradle(vararg args: String): BuildResult =
        GradleRunner.create()
            .withProjectDir(projectDir)
            .withPluginClasspath()
            .withDebug(true)
            .withArguments(listOf(*args) + "--stacktrace")
            .build()
}
