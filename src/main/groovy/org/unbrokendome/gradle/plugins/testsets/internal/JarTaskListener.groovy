package org.unbrokendome.gradle.plugins.testsets.internal

import org.gradle.api.Project
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.tasks.bundling.Jar
import org.unbrokendome.gradle.plugins.testsets.dsl.TestSet
import org.unbrokendome.gradle.plugins.testsets.dsl.TestSetContainer


class JarTaskListener {

    private final Project project


    JarTaskListener(Project project) {
        this.project = project

        def testSets = project.testSets as TestSetContainer
        testSets.whenObjectAdded { testSetAdded(it) }
    }


    void testSetAdded(TestSet testSet) {

        def jarTask = project.tasks.create(testSet.jarTaskName, Jar) {
            description = "Assembles a jar archive containing the ${testSet.name} classes."
            group = BasePlugin.BUILD_GROUP
            from {
                project.sourceSets[testSet.sourceSetName].output
            }
        }

        jarTask.conventionMapping.with {
            map('classifier') { testSet.classifier }
        }
    }
}
