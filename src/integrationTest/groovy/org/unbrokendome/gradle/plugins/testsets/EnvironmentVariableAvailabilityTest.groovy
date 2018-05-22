package org.unbrokendome.gradle.plugins.testsets

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import java.nio.file.Files

class EnvironmentVariableAvailabilityTest extends Specification {

    @Rule
    TemporaryFolder projectDir

    File buildFile

    def setup() {
        buildFile = projectDir.newFile('build.gradle')

        buildFile << '''
                plugins {
                    id 'groovy'
                    id "org.unbroken-dome.test-sets"
                }
                
                repositories {
                    jcenter()
                }
                
                dependencies {
                    testCompile "junit:junit:4.12"
                    testCompile "org.codehaus.groovy:groovy-all:2.4.13"
                    testCompile "org.spockframework:spock-core:1.1-groovy-2.4" 
                }
                
                testSets {
                    integrationTest {
                        environmentVariables = ["TESTVAR" : "TESTVALUE"] 
                    }
                }
            '''
        def testSourceDir = projectDir.root.toPath().resolve('src/integrationTest/groovy')
        Files.createDirectories(testSourceDir)
        def testFilePath = testSourceDir.resolve("TestSpec.groovy")
        def testFile = Files.createFile(testFilePath).toFile()

        testFile << '''
                import spock.lang.Specification
                
                class TestSpec extends Specification {
                    
                    def "Environment variable is available."() {
                        expect:
                        System.getenv("TESTVAR") == "TESTVALUE"
                    } 
                }
            '''
    }

    def "Environment variable is available in test."() {
        when:
            def result = GradleRunner.create()
                    .withProjectDir(projectDir.root)
                    .withPluginClasspath()
                    .withArguments('integrationTest', '--stacktrace')
                    .withDebug(true)
                    .forwardOutput()
                    .build()

        then:

            result.task(':integrationTest').outcome == TaskOutcome.SUCCESS
    }

}
