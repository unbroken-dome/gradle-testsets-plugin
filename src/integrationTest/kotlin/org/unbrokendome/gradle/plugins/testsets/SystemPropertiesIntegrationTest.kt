package org.unbrokendome.gradle.plugins.testsets

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.prop
import org.gradle.testkit.runner.BuildTask
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource


class SystemPropertiesIntegrationTest : AbstractGradleIntegrationTest() {

    @ParameterizedTest
    @ValueSource(strings = [
        """systemProperties = mapOf("TESTVAR" to "TESTVALUE")""",
        """systemProperties(mapOf("TESTVAR" to "TESTVALUE"))""",
        """systemProperty("TESTVAR", "TESTVALUE")"""
    ])
    fun `should pass system property to test execution`(sysPropStatement: String) {

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
                        $sysPropStatement
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
                file("SystemPropertiesTest.java", """
                    import static org.junit.jupiter.api.Assertions.*;
                    import org.junit.jupiter.api.Test;
                    
                    class SystemPropertiesTest {
                        
                        @Test
                        void shouldHaveSystemPropertiesAvailable() {
                            String value = System.getProperty("TESTVAR");
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

    @Test
    fun `should inherit existing system property variables`() {
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
                        systemProperty("TESTVAR", "TESTVALUE")
                    }
                }
                
                dependencies {
                    testImplementation("org.junit.jupiter:junit-jupiter-api:5.5.2")
                    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.5.2")
                }
                
                tasks.withType<Test> {
                    useJUnitPlatform()
                    jvmArgs("-DTESTVAR2=TESTVALUE2")
                }
            """)

            directory("src/integrationTest/java") {
                file("SystemPropertiesTest.java", """
                    import static org.junit.jupiter.api.Assertions.*;
                    import org.junit.jupiter.api.Test;
                    
                    class SystemPropertiesTest {
                        
                        @Test
                        void shouldHaveSystemPropertiesAvailable() {
                            String value = System.getProperty("TESTVAR");
                            assertEquals("TESTVALUE", value);
                            String value2 = System.getProperty("TESTVAR2");
                            assertEquals("TESTVALUE2", value2);
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
