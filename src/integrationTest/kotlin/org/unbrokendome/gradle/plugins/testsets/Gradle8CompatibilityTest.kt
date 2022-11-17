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


class Gradle8CompatibilityTest : AbstractGradleIntegrationTest() {

    abstract inner class AbstractVersionsCompatibilityTest {

        @ValueSource(strings = [
             "7.5.1", "7.6-rc-3"
        ])
        @ParameterizedTest(name = "Gradle {0}")
        @DisplayName("Should work in Gradle version")
        fun shouldWorkInGradleVersion(gradleVersion: String) {
            val result = GradleRunner.create()
                .withProjectDir(projectDir)
                .withGradleVersion(gradleVersion)
                .withPluginClasspath()
                .withArguments("integrationTest", "--info", "--stacktrace", "--warning-mode=fail")
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
    inner class GroovySinple : AbstractVersionsCompatibilityTest() {

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

    @Nested
    inner class KotlinSimple : AbstractVersionsCompatibilityTest() {

        @BeforeEach
        fun setup() {
            directory(projectDir) {
                file("build.gradle.kts", """ 
                plugins {
                    id("org.unbroken-dome.test-sets")
                }
                
                testSets { create("integrationTest") }
            """)
            }
        }
    }

    @Nested
    inner class GroovyExtendsOtherTestSet : AbstractVersionsCompatibilityTest() {

        @BeforeEach
        fun setup() {
            directory(projectDir) {
                file("build.gradle", """ 
                plugins {
                    id('org.unbroken-dome.test-sets')
                }
                
                testSets {
                    extended
                    
                    integrationTest {
                        extendsFrom extended
                    }
                }
            """)
            }
        }
    }

    @Nested
    inner class KotlinExtendsOtherTestSet : AbstractVersionsCompatibilityTest() {

        @BeforeEach
        fun setup() {
            directory(projectDir) {
                file("build.gradle.kts", """ 
                plugins {
                    id("org.unbroken-dome.test-sets")
                }
                
                testSets {
                    create("extended")
                    
                    create("integrationTest") {
                        extendsFrom("extended")
                    }
                }
            """)
            }
        }
    }

    @Nested
    inner class GroovyLibraries : AbstractVersionsCompatibilityTest() {

        @BeforeEach
        fun setup() {
            directory(projectDir) {
                file("build.gradle", """ 
                plugins {
                    id('org.unbroken-dome.test-sets')
                }
                
                testSets {
                    libraries { testCommon }

                    integrationTest {
                        imports 'testCommon'
                    }
                }
            """)
            }
        }
    }

    @Nested
    inner class KotlinLibraries : AbstractVersionsCompatibilityTest() {

        @BeforeEach
        fun setup() {
            directory(projectDir) {
                file("build.gradle.kts", """ 
                plugins {
                    id("org.unbroken-dome.test-sets")
                }
                
                testSets {
                    val testCommon by libraries.creating

                    create("integrationTest") {
                        imports(testCommon)
                    }
                }
            """)
            }
        }
    }
}
