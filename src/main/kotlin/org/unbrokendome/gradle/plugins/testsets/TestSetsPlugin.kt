package org.unbrokendome.gradle.plugins.testsets

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.testing.Test
import org.gradle.internal.reflect.Instantiator
import org.gradle.plugins.ide.eclipse.EclipsePlugin
import org.gradle.plugins.ide.eclipse.model.Classpath
import org.gradle.plugins.ide.eclipse.model.EclipseModel
import org.gradle.plugins.ide.eclipse.model.SourceFolder
import org.gradle.plugins.ide.idea.IdeaPlugin
import org.gradle.testing.jacoco.plugins.JacocoPlugin
import org.gradle.testing.jacoco.tasks.JacocoReport
import org.unbrokendome.gradle.plugins.testsets.dsl.*
import org.unbrokendome.gradle.plugins.testsets.internal.ConfigurationObserver
import org.unbrokendome.gradle.plugins.testsets.internal.IdeaModuleObserver
import org.unbrokendome.gradle.plugins.testsets.internal.SourceSetObserver
import org.unbrokendome.gradle.plugins.testsets.util.extension
import org.unbrokendome.gradle.plugins.testsets.util.get
import org.unbrokendome.gradle.plugins.testsets.util.registerOrConfigure
import org.unbrokendome.gradle.plugins.testsets.util.sourceSets
import javax.inject.Inject


class TestSetsPlugin
@Inject constructor(
    private val instantiator: Instantiator
) : Plugin<Project> {


    override fun apply(project: Project) {

        project.plugins.apply(JavaPlugin::class.java)

        val testSets = project.testSetContainer(instantiator)
        project.extensions.add(TestSetContainer::class.java, "testSets", testSets)

        val observers = listOf(
            SourceSetObserver(project),
            ConfigurationObserver(project)
        )

        testSets.all { testSet ->
            project.createConfigurationsForTestSet(testSet)
            project.addJarTaskFromTestSet(testSet)
            project.addArtifactsFromTestSet(testSet)

            (testSet as? TestSetBaseInternal)?.addObservers(observers)
        }

        testSets.withType(TestSet::class.java) { testSet ->
            project.addTestTaskFor(testSet)
        }

        project.plugins.withType(JacocoPlugin::class.java) {
            testSets.withType(TestSet::class.java) { testSet ->
                project.addJacocoReportTaskFor(testSet)
            }
        }

        project.plugins.withType(EclipsePlugin::class.java) {
            testSets.all { testSet ->
                project.modifyEclipseClasspath(testSet)
            }
        }

        project.plugins.withType(IdeaPlugin::class.java) {
            testSets.all { testSet ->
                project.modifyIdeaModule(testSet)
            }
        }
    }


    private fun Project.createConfigurationsForTestSet(testSet: TestSetBase) {
        project.configurations.run {

            registerOrConfigure(testSet.runtimeElementsConfigurationName) { conf ->
                conf.isVisible = false
                conf.isCanBeResolved = false
                conf.extendsFrom(
                    this[testSet.implementationConfigurationName],
                    this[testSet.runtimeOnlyConfigurationName]
                )
            }

            if (testSet is TestLibrary) {
                registerOrConfigure(testSet.apiConfigurationName) { conf ->
                    conf.isVisible = false
                    conf.isCanBeResolved = false
                    conf.isCanBeConsumed = false
                }

                registerOrConfigure(testSet.apiElementsConfigurationName) { conf ->
                    conf.isVisible = false
                    conf.isCanBeResolved = false
                    conf.isCanBeConsumed = true
                    conf.extendsFrom(this[testSet.apiConfigurationName])
                }

            }
        }
    }


    private fun Project.addJarTaskFromTestSet(testSet: TestSetBase) =
        tasks.registerOrConfigure(testSet.jarTaskName, Jar::class) { task ->
            task.description = "Assembles a jar archive containing the ${testSet.name} classes."
            task.group = BasePlugin.BUILD_GROUP
            task.from(testSet.sourceSet.output)

            // Classifier is deprecated, but the new archiveClassifier was added only in Gradle 5.1
            // For compatibility we will still use the old one
            @Suppress("DEPRECATION")
            task.classifier = testSet.classifier
        }


    @Suppress("NestedLambdaShadowedImplicitParameter")
    private fun Project.addArtifactsFromTestSet(testSet: TestSetBase) {

        configurations.registerOrConfigure(testSet.artifactConfigurationName) { conf ->
            conf.isCanBeResolved = false
            conf.extendsFrom(configurations[testSet.runtimeElementsConfigurationName])
        }

        afterEvaluate {
            if (testSet.createArtifact) {
                val jarTask = tasks.named(testSet.jarTaskName)
                val artifact = artifacts.add(
                    testSet.runtimeElementsConfigurationName,
                    jarTask
                )

                if (testSet is TestLibrary) {
                    artifacts.add(
                        testSet.apiElementsConfigurationName,
                        artifact
                    )
                }
            }
        }
    }


    private fun Project.addTestTaskFor(testSet: TestSet) {

        tasks.registerOrConfigure(testSet.testTaskName, Test::class) { task ->
            task.group = JavaBasePlugin.VERIFICATION_GROUP
            task.description = "Runs the ${testSet.name} tests."

            testSet.sourceSet.let { sourceSet ->
                task.testClassesDirs = sourceSet.output.classesDirs
                task.classpath = sourceSet.runtimeClasspath
            }
        }
    }


    private fun Project.addJacocoReportTaskFor(testSet: TestSet) {

        // JacocoReport tasks cannot be registered with register() because they install an afterEvaluate hook
        // which Gradle forbids when inside a deferred configuration block
        tasks.maybeCreate(testSet.jacocoReportTaskName, JacocoReport::class.java).also { task ->
            task.group = JavaBasePlugin.VERIFICATION_GROUP
            task.description = "Generates code coverage report for the ${testSet.testTaskName} tests."

            val testTask = tasks.getByName(testSet.testTaskName)
            task.executionData(testTask)
            task.sourceSets(this.sourceSets[SourceSet.MAIN_SOURCE_SET_NAME])
        }
    }


    @Suppress("NestedLambdaShadowedImplicitParameter")
    private fun Project.modifyEclipseClasspath(testSet: TestSetBase) {
        val eclipseModel: EclipseModel = extension()
        with(eclipseModel.classpath) {
            plusConfigurations.addAll(
                listOf(
                    configurations[testSet.compileClasspathConfigurationName],
                    configurations[testSet.runtimeClasspathConfigurationName]
                )
            )

            // Mark the source folders for the test set to contain test code
            file.whenMerged {
                val classpath = it as Classpath
                classpath.entries.asSequence()
                    .filterIsInstance<SourceFolder>()
                    .filter { it.entryAttributes["gradle_scope"] == testSet.sourceSetName }
                    .forEach { it.entryAttributes["test"] = true }
            }
        }
    }


    private fun Project.modifyIdeaModule(testSet: TestSetBase) {
        // the initial setup logic is contained in the IdeaModuleObserver class too
        val observer = IdeaModuleObserver(this, testSet)
        (testSet as? TestSetBaseInternal)?.addObserver(observer)
    }
}
