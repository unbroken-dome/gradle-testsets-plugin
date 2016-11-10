package org.unbrokendome.gradle.plugins.testsets

import org.gradle.api.Project
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.testfixtures.ProjectBuilder

import spock.lang.Specification

import static org.hamcrest.Matchers.*
import static spock.util.matcher.HamcrestSupport.*


class ArtifactTest extends Specification {

    Project project


    def setup() {
        project = ProjectBuilder.builder().build()
        project.apply plugin: 'org.unbroken-dome.test-sets'
    }


    def "New test set should have associated artifact configuration"() {
        when:
            project.testSets {
                myTest { createArtifact = true }
            }
        and:
            project.evaluate()

        then:
            project.configurations['myTest']
    }


    def "New test set should have associated artifact"() {
        when:
            project.testSets {
                myTest { createArtifact = true }
            }
        and:
            project.evaluate()

        then:
            expect project.configurations['myTest'].artifacts, not(empty())
    }
}
