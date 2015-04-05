package org.unbrokendome.gradle.plugins.testsets.internal

import org.gradle.api.Project
import org.gradle.api.artifacts.ConfigurationContainer
import org.unbrokendome.gradle.plugins.testsets.dsl.TestSet
import org.unbrokendome.gradle.plugins.testsets.dsl.TestSetContainer

class ConfigurationDependencyListener {

	private final Project project;


	ConfigurationDependencyListener(Project project) {
		this.project = project;

        def testSets = project.testSets as TestSetContainer
        testSets.whenObjectAdded { testSetAdded(it) }
	}


    void testSetAdded(TestSet testSet) {
        testSet.whenExtendsFromAdded { extendsFromAdded(testSet, it) }
    }


	void extendsFromAdded(TestSet testSet, TestSet superTestSet) {
		addConfigurationExtension testSet.compileConfigurationName, superTestSet.compileConfigurationName
		addConfigurationExtension testSet.runtimeConfigurationName, superTestSet.runtimeConfigurationName
	}


	private void addConfigurationExtension(String configurationName, String superConfigurationName) {
		ConfigurationContainer configurations = project.configurations;
        configurations[configurationName].extendsFrom configurations[superConfigurationName]
	}
}
