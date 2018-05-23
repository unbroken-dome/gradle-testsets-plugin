package org.unbrokendome.gradle.plugins.testsets

import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.testfixtures.ProjectBuilder

import spock.lang.Specification


class TestTaskTest extends Specification {

    Project project


    def setup() {
        project = ProjectBuilder.builder().build()
        project.apply plugin: 'org.unbroken-dome.test-sets'
    }


    def "New test set should have associated test task"() {
        when:
            project.testSets { myTest }

        then:
            project.tasks['myTest'] instanceof Test
    }


    def "HTML report output directory should be the name of the test set"() {
        when:
            project.testSets { myTest }

        then:
            def testTask = project.tasks['myTest'] as Test
            def htmlReportDir = project.file(testTask.reports.html.destination)
            htmlReportDir == new File(project.testReportDir, 'myTest')
    }


    def "JUnitXML report output directory should be the based on the name of the test set"() {
        when:
            project.testSets { myTest }

        then:
            def testTask = project.tasks['myTest'] as Test
            def junitXmlReportDir = project.file(testTask.reports.junitXml.destination)
            junitXmlReportDir == new File(project.testResultsDir, 'myTest')
    }


    def "testClassesDirs should include the custom test set (in Gradle 4.0)"() {
        when:
            project.testSets { myTest }

        then:
            def testTask = project.tasks['myTest'] as Test
            testTask.testClassesDirs[0] == project.sourceSets.myTest.output.classesDirs.singleFile
            testTask.testClassesDirs.files.size() == 1
    }

    def "testSet with environment variables specified are on the testSet's test task."() {
        when:
            project.testSets {
                myTest {
                    environmentVariables = ["TESTVAR" : "hello", "ANOTHERVAR" : 123]
                }
            }

        then:
            def testTask = project.tasks['myTest'] as Test
            testTask.environment.containsKey('TESTVAR')
            testTask.environment['TESTVAR'] == "hello"
            testTask.environment.containsKey('ANOTHERVAR')
            testTask.environment['ANOTHERVAR'] == 123
    }

    def "testSet with system properties specified are on the testSet's test task."() {
        when:
            project.testSets {
                myTest {
                    systemProperties = ["sysProp1" : "hello1", "sysProp2" : 123]
                }
            }

        then:
            def testTask = project.tasks["myTest"] as Test
            testTask.systemProperties.containsKey("sysProp1")
            testTask.systemProperties["sysProp1"] == "hello1"
            testTask.systemProperties.containsKey("sysProp2")
            testTask.systemProperties["sysProp2"] == 123
    }
}
