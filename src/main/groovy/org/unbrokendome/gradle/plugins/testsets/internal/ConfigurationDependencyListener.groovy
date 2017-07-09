package org.unbrokendome.gradle.plugins.testsets.internal

import org.gradle.api.Project
import org.gradle.api.artifacts.ConfigurationContainer
import org.unbrokendome.gradle.plugins.testsets.dsl.TestSet
import org.unbrokendome.gradle.plugins.testsets.dsl.TestSetContainer


class ConfigurationDependencyListener {

    private final Project project


    ConfigurationDependencyListener(Project project) {
        this.project = project

        def testSets = project.testSets as TestSetContainer
        testSets.whenObjectAdded { testSetAdded(it) }
    }


    void testSetAdded(TestSet testSet) {
        testSet.whenExtendsFromAdded { extendsFromAdded(testSet, it) }
    }


    void extendsFromAdded(TestSet testSet, TestSet superTestSet) {
        addConfigurationExtension testSet.compileConfigurationName, superTestSet.compileConfigurationName
        addConfigurationExtension testSet.implementationConfigurationName, superTestSet.implementationConfigurationName
        addConfigurationExtension testSet.runtimeConfigurationName, superTestSet.runtimeConfigurationName
        addConfigurationExtension testSet.runtimeOnlyConfigurationName, superTestSet.runtimeOnlyConfigurationName
    }


    private void addConfigurationExtension(String configurationName, String superConfigurationName) {

        if (!configurationName || !superConfigurationName) {
            return
        }

        ConfigurationContainer configurations = project.configurations

        def configuration = configurations.findByName(configurationName)
        def superConfiguration = configurations.findByName(superConfigurationName)

        if (configuration != null && superConfiguration != null) {
            configuration.extendsFrom superConfiguration
        }
    }
}
