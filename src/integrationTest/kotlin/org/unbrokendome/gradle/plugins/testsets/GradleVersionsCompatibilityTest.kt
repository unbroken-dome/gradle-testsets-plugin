package org.unbrokendome.gradle.plugins.testsets

import assertk.assertThat
import assertk.assertions.isIn
import assertk.assertions.isNotNull
import assertk.assertions.prop
import org.gradle.testkit.runner.BuildTask
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource


class GradleVersionsCompatibilityTest : AbstractGradleIntegrationTest() {

    abstract inner class AbstractVersionsCompatibilityTest {

        @ValueSource(strings = [
            "5.1.1", "5.6.4", "6.0.1", "6.8.3", "7.0", "7.5.1", "8.0", "8.3"
        ])
        @ParameterizedTest(name = "Gradle {0}")
        @DisplayName("Should work in Gradle version")
        fun shouldWorkInGradleVersion(gradleVersion: String) {
            val result = GradleRunner.create()
                .withProjectDir(projectDir)
                .withGradleVersion(gradleVersion)
                .withPluginClasspath()
                .withArguments("integrationTest", "--info", "--stacktrace")
                .forwardOutput()
                .build()

            assertThat(result, "result")
                .prop("for task integrationTest") { it.task(":integrationTest") }
                .isNotNull()
                .prop("outcome", BuildTask::getOutcome)
                .isIn(TaskOutcome.NO_SOURCE, TaskOutcome.UP_TO_DATE)
        }
    }


    @Nested
    inner class KotlinDsl : AbstractVersionsCompatibilityTest() {

        @BeforeEach
        fun setup() {

            directory(projectDir) {
                file("build.gradle.kts", """ 
                plugins {
                    id("org.unbroken-dome.test-sets")
                }
                
                testSets.create("integrationTest")
            """)
            }
        }
    }


    @Nested
    inner class GroovyDsl : AbstractVersionsCompatibilityTest() {

        @BeforeEach
        fun setup() {

            directory(projectDir) {
                file("build.gradle", """ 
                plugins {
                    id('org.unbroken-dome.test-sets')
                }
                
                testSets { integrationTest }
            """)
            }
        }
    }
}
