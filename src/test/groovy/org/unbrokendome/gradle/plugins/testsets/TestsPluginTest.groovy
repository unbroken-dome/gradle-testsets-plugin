package org.unbrokendome.gradle.plugins.testsets

import org.gradle.api.Project
import org.gradle.api.artifacts.DependencySet
import org.gradle.testfixtures.ProjectBuilder

import spock.lang.Specification


public class TestsPluginTest extends Specification {

    Project project


    def setup() {
        project = ProjectBuilder.builder().build()
        project.apply plugin: 'org.unbroken-dome.test-sets'
    }


    def "Test sets extension should be created"() {
        expect:
            project.testSets
    }


    def "New test set's compile configuration should extend the testCompile configuration"() {
        when:
            project.testSets { myTest }

        then:
            project.configurations['myTestCompile'].extendsFrom == [ project.configurations['testCompile'] ].toSet()
    }


    def "New test set should have an associated runtime configuration"() {
        when:
            project.testSets { myTest }

        then:
            project.configurations['myTestRuntime']
    }


    def "New test set's runtime configuration should extend the testRuntime configuration"() {
        when:
            project.testSets { myTest }

        then:
            project.configurations['myTestRuntime'].extendsFrom.contains project.configurations['testRuntime']
    }
}
