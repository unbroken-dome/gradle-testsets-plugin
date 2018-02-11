package org.unbrokendome.gradle.plugins.testsets

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder

import spock.lang.Specification


class SourceSetTest extends Specification {

    Project project


    def setup() {
        project = ProjectBuilder.builder().build()
        project.apply plugin: 'org.unbroken-dome.test-sets'
    }


    def "New test set should have an associated source set"() {
        when:
            project.testSets { myTest }

        then:
            project.sourceSets['myTest']
    }


    def "New test set should have an associated compile configuration"() {
        when:
            project.testSets { myTest }

        then:
            project.configurations['myTestCompile']
    }


    def "New test set should have an associated implementation configuration"() {
        when:
            project.testSets { myTest }

        then:
            project.configurations['myTestImplementation']
    }


    def "New test set should have an associated runtime configuration"() {
        when:
            project.testSets { myTest }

        then:
            project.configurations['myTestRuntime']
    }


    def "New test set should have an associated runtime-only configuration"() {
        when:
            project.testSets { myTest }

        then:
            project.configurations['myTestRuntimeOnly']
    }


    def "New test set's compile configuration should depend on the main source set's output"() {
        when:
            project.testSets { myTest }

        then:
            project.configurations['myTestCompile'].dependencies.any { dep ->
                dep.contentEquals project.dependencies.create(project.sourceSets['main'].output)
            }
    }


    def "Source set should use test set's dirName for java srcDir if given"() {
        when:
            project.testSets {
                myTest { dirName = 'my-test' }
            }

        then:
            project.sourceSets['myTest'].java.srcDirs == [ project.file('src/my-test/java') ] as Set
    }


    def "Source set should use test set's dirName for resources srcDir if given"() {
        when:
            project.testSets {
                myTest { dirName = 'my-test' }
            }

        then:
            project.sourceSets['myTest'].resources.srcDirs == [ project.file('src/my-test/resources') ] as Set
    }


    def "Source set should use test set's dirName for groovy srcDir if given"() {
        when:
            project.apply plugin: 'groovy'
            project.testSets {
                myTest { dirName = 'my-test' }
            }

        then:
            project.sourceSets['myTest'].groovy.srcDirs == [ project.file('src/my-test/groovy') ] as Set
    }


    def "Source set should use test set's dirName for kotlin srcDir if given"() {
        when:
            project.apply plugin: 'org.jetbrains.kotlin.jvm'
            project.testSets {
                myTest { dirName = 'my-test' }
            }

        then:
            project.sourceSets['myTest'].kotlin.srcDirs == [ project.file('src/my-test/kotlin') ] as Set
    }
}
