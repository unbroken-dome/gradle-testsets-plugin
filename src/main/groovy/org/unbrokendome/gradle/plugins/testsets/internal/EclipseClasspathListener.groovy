package org.unbrokendome.gradle.plugins.testsets.internal

import org.gradle.api.Project
import org.gradle.plugins.ide.eclipse.model.EclipseClasspath
import org.gradle.plugins.ide.eclipse.model.EclipseModel
import org.unbrokendome.gradle.plugins.testsets.dsl.TestSet
import org.unbrokendome.gradle.plugins.testsets.dsl.TestSetContainer

class EclipseClasspathListener {

	private final Project project;


	EclipseClasspathListener(Project project) {
		this.project = project

        def testSets = project.testSets as TestSetContainer
        testSets.whenObjectAdded { testSetAdded(it) }
	}


	void testSetAdded(TestSet testSet) {
		
		def eclipseModel = project.extensions.findByType EclipseModel
		if (eclipseModel) {

			def eclipseClasspath = eclipseModel.classpath;

            addConfigurationToClasspath testSet.compileConfigurationName, eclipseClasspath
            addConfigurationToClasspath testSet.runtimeConfigurationName, eclipseClasspath
		}
	}


    private void addConfigurationToClasspath(String configurationName, EclipseClasspath eclipseClasspath) {
        def testSetCompileConfiguration = project.configurations.findByName configurationName
        if (testSetCompileConfiguration) {
            eclipseClasspath.plusConfigurations.add testSetCompileConfiguration
        }
    }
}
