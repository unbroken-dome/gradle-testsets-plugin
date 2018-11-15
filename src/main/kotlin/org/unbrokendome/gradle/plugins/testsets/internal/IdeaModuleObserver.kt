package org.unbrokendome.gradle.plugins.testsets.internal

import org.gradle.api.Project
import org.gradle.plugins.ide.idea.model.IdeaModel
import org.unbrokendome.gradle.plugins.testsets.dsl.TestSetBase
import org.unbrokendome.gradle.plugins.testsets.dsl.TestSetObserver
import org.unbrokendome.gradle.plugins.testsets.util.extension
import java.io.File


/**
 * Modifies the IDEA module configuration so that test set source and resource directories are properly
 * marked as "test" in IntelliJ IDEA.
 *
 * This observer is stateful, so a separate instance needs to be used for each testSet.
 */
internal class IdeaModuleObserver(
        private val project: Project,
        private val testSet: TestSetBase)
    : TestSetObserver {

    private val ideaModel: IdeaModel = project.extension()
    private val ideaModule = ideaModel.module

    private var sourceDirs: Set<File> = emptySet()
    private var resourceDirs: Set<File> = emptySet()


    init {
        updateDirectories()
        updateConfigurations()
    }


    override fun dirNameChanged(testSet: TestSetBase, oldDirName: String, newDirName: String) {

        assert(testSet == this.testSet)

        // First, remove any old source and resource directories. Note that we cannot clear these sets,
        // since the IDEA module might have other test source/resource directories.
        ideaModule.testSourceDirs.removeAll(sourceDirs)
        ideaModule.testResourceDirs.removeAll(resourceDirs)

        updateDirectories()
    }


    private fun updateDirectories() {
        with (testSet.sourceSet) {
            sourceDirs = getAllCodeSourceDirectorySets()
                    .map { it.srcDirs }
                    .flatten()
                    .toSet()
            resourceDirs = resources.srcDirs.toSet()
        }

        ideaModule.testSourceDirs = ideaModule.testSourceDirs + sourceDirs
        ideaModule.testResourceDirs = ideaModule.testResourceDirs + resourceDirs
    }


    private fun updateConfigurations() {
        val configurations = testSet.run {
            listOf(compileClasspathConfigurationName,
                    runtimeClasspathConfigurationName,
                    annotationProcessorConfigurationName)
        }.mapNotNull(project.configurations::findByName)

        ideaModule.scopes["TEST"]?.get("plus")?.addAll(configurations)
    }
}
