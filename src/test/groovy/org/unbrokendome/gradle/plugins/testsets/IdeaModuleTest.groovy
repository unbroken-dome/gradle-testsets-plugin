package org.unbrokendome.gradle.plugins.testsets

import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification


class IdeaModuleTest extends Specification {

    Project project


    def setup() {
        project = ProjectBuilder.builder().build()
        project.apply plugin: 'org.unbroken-dome.test-sets'
        project.apply plugin: 'idea'
    }


    def "Test set source directories should be added to module"() {
        when:
            project.testSets { myTest }

        then:
            project.idea.module.testSourceDirs.containsAll testSetSourceDirs()
    }


    def "Test set scopes should be added to module"() {
        when:
            project.testSets { myTest }

        then:
            project.idea.module.scopes.TEST.plus.containsAll testSetConfigurations()
    }


    private Set<File> testSetSourceDirs() {
        [ project.file('src/myTest/java') ] as Set
    }


    private Set<Configuration> testSetConfigurations() {
        [ project.configurations.getByName('myTestAnnotationProcessor'),
          project.configurations.getByName('myTestCompileClasspath'),
          project.configurations.getByName('myTestRuntimeClasspath') ] as Set
    }
}
