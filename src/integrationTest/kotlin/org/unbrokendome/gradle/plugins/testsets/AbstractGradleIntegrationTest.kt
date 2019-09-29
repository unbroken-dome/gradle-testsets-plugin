package org.unbrokendome.gradle.plugins.testsets

import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInfo
import java.io.File
import java.nio.file.Files


abstract class AbstractGradleIntegrationTest {

    protected lateinit var projectDir: File

    protected val buildDir: File
        get() = projectDir.resolve("build")


    @BeforeEach
    fun setupProject(testInfo: TestInfo) {
        projectDir = Files.createTempDirectory("gradle").toFile()
        val projectName = projectDir.name

        // Always create a settings file, otherwise Gradle searches up the directory hierarchy
        // (and might actually find another file)
        directory(projectDir) {
            file("settings.gradle", contents = """ 
                rootProject.name = '$projectName'
            """)
        }
    }


    @AfterEach
    fun cleanupProject() {
        projectDir.deleteRecursively()
    }


    protected fun runGradle(vararg args: String) =
        GradleRunner.create()
            .withProjectDir(projectDir)
            .withPluginClasspath()
            .withDebug(true)
            .withArguments(listOf(*args) + "--stacktrace")
            .build()
}
