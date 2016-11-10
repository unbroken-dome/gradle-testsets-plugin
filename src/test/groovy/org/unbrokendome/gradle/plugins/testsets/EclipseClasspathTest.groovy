package org.unbrokendome.gradle.plugins.testsets

import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.testfixtures.ProjectBuilder

import spock.lang.Specification


class EclipseClasspathTest extends Specification {

    Project project


    def setup() {
        project = ProjectBuilder.builder().build()
        project.apply plugin: 'eclipse'
        project.apply plugin: 'org.unbroken-dome.test-sets'
    }


    def "Test set classpath should be added to classpath container"() {
        when:
            project.testSets { myTest }

        then:
            project.eclipse.classpath
    }


}
