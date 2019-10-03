package org.unbrokendome.gradle.plugins.testsets

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.prop
import org.gradle.internal.file.DefaultFileMetadata.file
import org.gradle.testkit.runner.BuildTask
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource


class EnvironmentVariablesIntegrationTest : AbstractGradleIntegrationTest() {
    companion object {
        const val TEST_FILE = """
                    import static org.junit.jupiter.api.Assertions.*;
                    import org.junit.jupiter.api.Test;
                    
                    class EnvironmentTest {
                        
                        @Test
                        void shouldHaveEnvironmentAvailable() {
                            String value = System.getenv("TESTVAR");
                            assertEquals("TESTVALUE", value);
                        }
                    }
                """
    }

    @ParameterizedTest
    @ValueSource(strings = [
        """environment = mapOf("TESTVAR" to "TESTVALUE")""",
        """environment(mapOf("TESTVAR" to "TESTVALUE"))""",
        """environment("TESTVAR", "TESTVALUE")"""
    ])
    fun `should pass environment variable to test execution`(envVarStatement: String) {

        directory(projectDir) {
            file("build.gradle.kts", """
                plugins {
                    `java`
                    id("org.unbroken-dome.test-sets")
                }
                
                repositories {
                    jcenter()
                }
                
                testSets {
                    createTestSet("integrationTest") {
                        $envVarStatement
                    }
                }
                
                dependencies {
                    testImplementation("org.junit.jupiter:junit-jupiter-api:5.5.2")
                    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.5.2")
                }
                
                tasks.withType<Test> {
                    useJUnitPlatform()
                }
            """)

            directory("src/integrationTest/java") {
                file("EnvironmentTest.java", TEST_FILE)
            }

            val result = runGradle("integrationTest")

            assertThat(result, "result")
                .prop("for task integrationTest") { it.task(":integrationTest") }
                .isNotNull()
                .prop("outcome", BuildTask::getOutcome)
                .isEqualTo(TaskOutcome.SUCCESS)
        }
    }

    @Test
    fun `should inherit existing environment variables`() {
        directory(projectDir) {
            file("build.gradle.kts", """
                plugins {
                    `java`
                    id("org.unbroken-dome.test-sets")
                }
                
                repositories {
                    jcenter()
                }
                
                testSets {
                    createTestSet("integrationTest") { }
                }
                
                dependencies {
                    testImplementation("org.junit.jupiter:junit-jupiter-api:5.5.2")
                    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.5.2")
                }
                
                tasks.withType<Test> {
                    useJUnitPlatform()
                }
            """)

            directory("src/integrationTest/java") {
                file("EnvironmentTest.java", TEST_FILE)
            }

            val result = GradleRunner.create()
                    .withProjectDir(projectDir)
                    .withPluginClasspath()
                    .withArguments("integrationTest",  "-s")
                    .withEnvironment(mapOf("TESTVAR" to "TESTVALUE"))
                    .build()

            assertThat(result, "result")
                    .prop("for task integrationTest") { it.task(":integrationTest") }
                    .isNotNull()
                    .prop("outcome", BuildTask::getOutcome)
                    .isEqualTo(TaskOutcome.SUCCESS)
        }
    }


}