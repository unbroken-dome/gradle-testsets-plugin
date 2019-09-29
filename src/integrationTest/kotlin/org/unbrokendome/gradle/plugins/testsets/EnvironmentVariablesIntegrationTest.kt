package org.unbrokendome.gradle.plugins.testsets

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.prop
import org.gradle.testkit.runner.BuildTask
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource


class EnvironmentVariablesIntegrationTest : AbstractGradleIntegrationTest() {

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
                file("EnvironmentTest.java", """
                    import static org.junit.jupiter.api.Assertions.*;
                    import org.junit.jupiter.api.Test;
                    
                    class EnvironmentTest {
                        
                        @Test
                        void shouldHaveEnvironmentAvailable() {
                            String value = System.getenv("TESTVAR");
                            assertEquals("TESTVALUE", value);
                        }
                    }
                """)
            }

            val result = runGradle("integrationTest")

            assertThat(result, "result")
                .prop("for task integrationTest") { it.task(":integrationTest") }
                .isNotNull()
                .prop("outcome", BuildTask::getOutcome)
                .isEqualTo(TaskOutcome.SUCCESS)
        }

    }


}