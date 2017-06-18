package org.unbrokendome.gradle.plugins.testsets.internal

import org.gradle.api.Project
import org.gradle.plugins.ide.eclipse.model.Classpath
import org.gradle.plugins.ide.eclipse.model.ClasspathEntry
import org.gradle.plugins.ide.eclipse.model.EclipseClasspath
import org.gradle.plugins.ide.eclipse.model.EclipseModel
import org.unbrokendome.gradle.plugins.testsets.dsl.TestSet
import org.unbrokendome.gradle.plugins.testsets.dsl.TestSetContainer

import java.nio.file.Paths


class EclipseClasspathListener {

    private final Project project


    EclipseClasspathListener(Project project) {
        this.project = project

        def testSets = project.testSets as TestSetContainer
        testSets.whenObjectAdded { testSetAdded(it) }
    }


    void testSetAdded(TestSet testSet) {

        def eclipseModel = project.extensions.findByType EclipseModel
        if (eclipseModel) {

            def eclipseClasspath = eclipseModel.classpath

            addConfigurationToClasspath testSet.compileConfigurationName, eclipseClasspath
            addConfigurationToClasspath testSet.runtimeConfigurationName, eclipseClasspath

            applyClasspathFix eclipseClasspath
        }
    }


    private void addConfigurationToClasspath(String configurationName, EclipseClasspath eclipseClasspath) {
        def testSetCompileConfiguration = project.configurations.findByName configurationName
        if (testSetCompileConfiguration) {
            eclipseClasspath.plusConfigurations.add testSetCompileConfiguration
        }
    }

    /**
     * Removes classpath entries for the outputs of other source sets that the test set depends on.
     * The eclipse plugin adds them because of the dependency, but there is really no point in keeping them
     * because the sources are already in the classpath.
     *
     * @param eclipseClasspath the {@link EclipseClasspath} instance being configured
     */
    private void applyClasspathFix(EclipseClasspath eclipseClasspath) {

        eclipseClasspath.file.whenMerged { Classpath classpath ->

            // Get all output directories for other source sets' classes and resources.
            def outputPaths = eclipseClasspath.project.sourceSets*.output
                    .collect { it.classesDirs + [it.resourcesDir] }
                    .flatten { file -> Paths.get(file as String) } as Set
            classpath.entries.removeAll { ClasspathEntry entry ->
                entry.kind == 'lib' && Paths.get(entry.path) in outputPaths
            }
        }
    }
}
