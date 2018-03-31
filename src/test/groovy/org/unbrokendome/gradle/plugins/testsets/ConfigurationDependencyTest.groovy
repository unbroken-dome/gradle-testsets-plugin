package org.unbrokendome.gradle.plugins.testsets

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification
import spock.lang.Unroll


class ConfigurationDependencyTest extends Specification {

    Project project


    def setup() {
        project = ProjectBuilder.builder().build()
        project.apply plugin: 'org.unbroken-dome.test-sets'
    }


    @Unroll
    def "Extending another test set should extend the #configurationBaseName configuration"(
            String configurationBaseName, String extendedConfigurationName, String extendingConfigurationName) {
        when:
            project.testSets {
                foo
                bar.extendsFrom foo
            }

        then:
            project.configurations[extendedConfigurationName] in project.configurations[extendingConfigurationName].extendsFrom

        where:
            configurationBaseName | extendedConfigurationName | extendingConfigurationName
            'compile'             | 'fooCompile'              | 'barCompile'
            'compileOnly'         | 'fooCompileOnly'          | 'barCompileOnly'
            'implementation'      | 'fooImplementation'       | 'barImplementation'
            'runtime'             | 'fooRuntime'              | 'barRuntime'
            'runtimeOnly'         | 'fooRuntimeOnly'          | 'barRuntimeOnly'
    }
}
