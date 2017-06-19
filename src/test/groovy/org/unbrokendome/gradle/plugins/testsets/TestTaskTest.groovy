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
            testTask.testClassesDirs[0] == project.sourceSets.test.output.classesDirs.singleFile
            testTask.testClassesDirs[1] == project.sourceSets.myTest.output.classesDirs.singleFile
            testTask.testClassesDirs.files.size() == 2
    }
}
