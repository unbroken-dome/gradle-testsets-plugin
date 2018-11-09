package org.unbrokendome.gradle.plugins.testsets.internal

import org.gradle.api.Project
import org.gradle.api.internal.plugins.DslObject
import org.gradle.api.tasks.GroovySourceSet
import org.gradle.api.tasks.SourceSet
import org.gradle.plugins.ide.idea.model.*
import org.unbrokendome.gradle.plugins.testsets.dsl.TestSet
import org.unbrokendome.gradle.plugins.testsets.dsl.TestSetContainer

import java.nio.file.Paths


public class IdeaModuleListener {

    final Project project


    public IdeaModuleListener(Project project) {
        this.project = project

        def testSets = project.testSets as TestSetContainer
        testSets.whenObjectAdded { testSetAdded(it) }
    }


    void testSetAdded(TestSet testSet) {

        def ideaModel = project.extensions.findByType IdeaModel
        if (ideaModel) {

            def ideaModule = ideaModel.module

            def sourceSet = project.sourceSets[testSet.sourceSetName] as SourceSet

            def srcDirs = sourceSet.allJava.srcDirs
            ideaModule.testSourceDirs += srcDirs
            testSet.whenDirNameChanged {
                ideaModule.testSourceDirs -= srcDirs
                ideaModule.testSourceDirs += sourceSet.allJava.srcDirs
            }

            def groovySourceSet = new DslObject(sourceSet).convention.findPlugin(GroovySourceSet)
            if (groovySourceSet) {
                def groovySrcDirs = groovySourceSet.allGroovy.srcDirs
                ideaModule.testSourceDirs += groovySrcDirs
                testSet.whenDirNameChanged {
                    ideaModule.testSourceDirs -= groovySrcDirs
                    ideaModule.testSourceDirs += groovySourceSet.allGroovy.srcDirs
                }
            }

            ideaModule.iml.withXml {
                def moduleRootManager = it.asNode().component.find { it.@name == 'NewModuleRootManager' }
                def sourceFolderNode = moduleRootManager?.content?.sourceFolder?.last()
                sourceSet.resources.srcDirs.each { resourcesDir ->
                    def relPath = project.relativePath(resourcesDir.canonicalPath).replace('\\', '/')
                    sourceFolderNode += {
                        sourceFolder(url: 'file://$MODULE_DIR$/' + relPath,
                                type: 'java-test-resource')
                    }
                }
            }

            addConfigurationToClasspath testSet.compileClasspathConfigurationName, ideaModule
            addConfigurationToClasspath testSet.runtimeClasspathConfigurationName, ideaModule
            addConfigurationToClasspath testSet.annotationProcessorConfigurationName, ideaModule

            applyLibraryFix ideaModule
        }
    }


    private void addConfigurationToClasspath(String configurationName, IdeaModule ideaModule) {
        def testSetConfiguration = project.configurations.findByName configurationName
        if (testSetConfiguration) {
            ideaModule.scopes.TEST.plus += [ testSetConfiguration ]
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
            def outputPaths = project.sourceSets*.output
                    .collect { Compat.classesDirsFor(it) + [it.resourcesDir] }
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
