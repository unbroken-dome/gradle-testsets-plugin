package org.unbrokendome.gradle.plugins.testsets

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification
import spock.lang.Unroll


class GradleVersionsCompatibilityTest extends Specification {

    @Rule TemporaryFolder projectDir

    File buildFile


    def setup() {
        buildFile = projectDir.newFile('build.gradle')

        buildFile << '''
                plugins {
                    id 'org.unbroken-dome.test-sets'
                }
                
                testSets {
                    integrationTest
                }
            '''
    }


    @Unroll
    def "Plugin should work in Gradle #gradleVersion"(String gradleVersion) {
        when:
            def result = GradleRunner.create()
                    .withGradleVersion(gradleVersion)
                    .withProjectDir(projectDir.root)
                    .withPluginClasspath()
                    .withArguments('integrationTest', '--stacktrace')
                    .withDebug(true)
                    .forwardOutput()
                    .build()
        then:
            result.task(':integrationTest').outcome in [ TaskOutcome.NO_SOURCE, TaskOutcome.UP_TO_DATE ]

        where:
            gradleVersion << [ '4.6', '4.5.1', '4.0', '3.5', '3.3', '3.2.1', '2.14.1' ]
    }
}
