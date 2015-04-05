package org.unbrokendome.gradle.plugins.testsets.internal

import org.gradle.api.Project
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.testing.Test
import org.unbrokendome.gradle.plugins.testsets.dsl.TestSet
import org.unbrokendome.gradle.plugins.testsets.dsl.TestSetContainer

class TestTaskListener {

	final Project project


	TestTaskListener(Project project) {
		this.project = project;

        def testSets = project.testSets as TestSetContainer
        testSets.whenObjectAdded { testSetAdded(it) }
	}


	void testSetAdded(TestSet testSet) {
		def testTask = project.tasks.create testSet.testTaskName, Test

        testTask.conventionMapping.with {
            map('description') { "Runs the ${testSet.name} tasks" }

            map('group') { JavaBasePlugin.VERIFICATION_GROUP }

            map('testClassesDir') {
                def sourceSet = (SourceSet) project.sourceSets[testSet.sourceSetName]
                sourceSet.output.classesDir
            }

            map('classpath') {
                def sourceSet = (SourceSet) project.sourceSets[testSet.sourceSetName]
                sourceSet.runtimeClasspath
            }
        }
	}
}
