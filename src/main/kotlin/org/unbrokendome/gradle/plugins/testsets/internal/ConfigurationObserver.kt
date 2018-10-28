package org.unbrokendome.gradle.plugins.testsets.internal

import org.gradle.api.Project
import org.unbrokendome.gradle.plugins.testsets.dsl.TestLibrary
import org.unbrokendome.gradle.plugins.testsets.dsl.TestSetBase
import org.unbrokendome.gradle.plugins.testsets.dsl.TestSetObserver


internal class ConfigurationObserver(
        private val project: Project)
    : TestSetObserver {

    private companion object {
        val inheritedConfigurationNames: List<(TestSetBase) -> String> = listOf(
                TestSetBase::compileConfigurationName,
                TestSetBase::compileOnlyConfigurationName,
                TestSetBase::annotationProcessorConfigurationName,
                TestSetBase::implementationConfigurationName,
                TestSetBase::runtimeConfigurationName,
                TestSetBase::runtimeOnlyConfigurationName)
    }


    override fun extendsFromAdded(testSet: TestSetBase, added: TestSetBase) {
        for (configurationNameAccessor in inheritedConfigurationNames) {
            addConfigurationExtension(testSet, added, configurationNameAccessor)
        }
    }


    override fun importAdded(testSet: TestSetBase, added: TestLibrary) {

        val implementation = project.configurations.findByName(testSet.implementationConfigurationName)
        val importedApi = project.configurations.findByName(added.apiConfigurationName)

        if (implementation != null) {
            if (importedApi != null) {
                implementation.extendsFrom(importedApi)
            }
            implementation.dependencies.add(
                    project.dependencies.create(added.sourceSet.output))
        }

        val runtimeOnly = project.configurations.findByName(testSet.runtimeOnlyConfigurationName)
        val importedRuntimeClasspath = project.configurations.findByName(added.runtimeClasspathConfigurationName)
        if (runtimeOnly != null && importedRuntimeClasspath != null) {
            runtimeOnly.extendsFrom(importedRuntimeClasspath)
        }
    }


    private fun addConfigurationExtension(testSet: TestSetBase, superTestSet: TestSetBase,
                                          configurationNameAccessor: (TestSetBase) -> String) {

        val configuration = project.configurations.findByName(configurationNameAccessor(testSet))
        val superConfiguration = project.configurations.findByName(configurationNameAccessor(superTestSet))

        if (configuration != null && superConfiguration != null) {
            configuration.extendsFrom(superConfiguration)
        }
    }


}
