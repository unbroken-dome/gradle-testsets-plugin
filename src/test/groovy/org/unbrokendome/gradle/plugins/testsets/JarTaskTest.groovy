package org.unbrokendome.gradle.plugins.testsets

import org.gradle.api.Project
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.bundling.Jar
import org.gradle.testfixtures.ProjectBuilder

import static org.hamcrest.Matchers.*
import static spock.util.matcher.HamcrestSupport.*

import spock.lang.Specification


class JarTaskTest extends Specification {

    Project project


    def setup() {
        project = ProjectBuilder.builder().build()
        project.apply plugin: 'org.unbroken-dome.test-sets'
    }


    def "New test set should have associated jar task"() {
        when:
            project.testSets { myTest }

        then:
            project.tasks['myTestJar'] instanceof Jar
    }
}
