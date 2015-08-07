package org.unbrokendome.gradle.plugins.testsets.internal

import org.gradle.api.Project
import org.gradle.api.tasks.SourceSet
import org.gradle.plugins.ide.idea.model.*
import org.unbrokendome.gradle.plugins.testsets.dsl.TestSet
import org.unbrokendome.gradle.plugins.testsets.dsl.TestSetContainer

import java.nio.file.Paths

public class IdeaModuleListener {

    private final Project project;


    public IdeaModuleListener(Project project) {
        this.project = project;

        def testSets = project.testSets as TestSetContainer
        testSets.whenObjectAdded { testSetAdded(it) }
    }


    void testSetAdded(TestSet testSet) {

        def ideaModel = project.extensions.findByType IdeaModel
        if (ideaModel) {

            def ideaModule = ideaModel.module

            def sourceSet = project.sourceSets[testSet.sourceSetName] as SourceSet
            ideaModule.testSourceDirs += sourceSet.allSource.srcDirs

            ideaModule.scopes.TEST.plus += [ project.configurations[testSet.runtimeConfigurationName] ]

            applyLibraryFix ideaModule
        }
    }


    /**
     * Removes module-library entries for the outputs of other source sets that the test set depends on.
     * The idea plugin adds them because of the dependency, but there is really no point in keeping them
     * because the sources are already in the classpath.
     *
     * @param ideaModule the {@link IdeaModule} instance being configured
     */
    private void applyLibraryFix(IdeaModule ideaModule) {

        ideaModule.iml.whenMerged { Module module ->

            // Get all output directories for other source sets' classes and resources.
            def outputPaths = ideaModule.project.sourceSets*.output
                    .collect { [ it.classesDir, it.resourcesDir ]}
                    .flatten { file -> Paths.get(file as String) } as Set

            module.dependencies.removeAll { dep ->
                dep instanceof ModuleLibrary && ((ModuleLibrary) dep).classes.any { Path path ->

                    def canonicalUrl = URI.create(path.canonicalUrl)
                    if (canonicalUrl.scheme == 'file') {
                        // Add another slash to the scheme specific part if necessary... IDEA uses 2 forward slashes
                        // for file URLs, whereas java.nio uses 3
                        if (!canonicalUrl.schemeSpecificPart.startsWith('///')) {
                            canonicalUrl = new URI('file', '/' + canonicalUrl.schemeSpecificPart, null)
                        }
                        return Paths.get(canonicalUrl) in outputPaths
                    } else {
                        return false
                    }
                }
            }
        }
    }
}
