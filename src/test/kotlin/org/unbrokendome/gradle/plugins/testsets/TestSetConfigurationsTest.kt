package org.unbrokendome.gradle.plugins.testsets

import assertk.assert
import org.gradle.api.Project
import org.gradle.api.artifacts.FileCollectionDependency
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.unbrokendome.gradle.plugins.testsets.dsl.TestLibrary
import org.unbrokendome.gradle.plugins.testsets.dsl.testSets
import org.unbrokendome.gradle.plugins.testsets.testutils.containsDependency
import org.unbrokendome.gradle.plugins.testsets.testutils.containsItem
import org.unbrokendome.gradle.plugins.testsets.testutils.extendsFrom


@Suppress("NestedLambdaShadowedImplicitParameter")
class TestSetConfigurationsTest {

    private val project: Project = ProjectBuilder.builder().build()
            .also {
                it.plugins.apply(TestSetsPlugin::class.java)
            }


    @ParameterizedTest
    @ValueSource(strings = [
        "myTestCompile <- testCompile",
        "myTestCompileOnly <- testCompileOnly",
        "myTestAnnotationProcessor <- testAnnotationProcessor",
        "myTestImplementation <- testImplementation",
        "myTestRuntime <- testRuntime",
        "myTestRuntimeOnly <- testRuntimeOnly"])
    fun `New test set's configurations should extend the built-in test configurations`(configs: String) {
        project.testSets.create("myTest")

        val (configurationName, superConfigurationName) = configs.split(" <- ")

        assert(project.configurations, "configurations")
                .containsItem(configurationName) {
                    it.extendsFrom(superConfigurationName)
                }
    }


    @ParameterizedTest
    @ValueSource(strings = [
        "fooCompile <- barCompile",
        "fooCompileOnly <- barCompileOnly",
        "fooAnnotationProcessor <- barAnnotationProcessor",
        "fooImplementation <- barImplementation",
        "fooRuntime <- barRuntime",
        "fooRuntimeOnly <- barRuntimeOnly"])
    fun `Extending another test set should extend the configurations`(configs: String) {
        val fooTestSet = project.testSets.create("foo")
        val barTestSet = project.testSets.create("bar")
        fooTestSet.extendsFrom(barTestSet)

        val (configurationName, superConfigurationName) = configs.split(" <- ")

        assert(project.configurations, "configurations")
                .containsItem(configurationName) {
                    it.extendsFrom(superConfigurationName)
                }
    }


    @Nested
    inner class Library {

        private val barLibrary: TestLibrary = project.testSets.createLibrary("bar")

        init {
            project.testSets.create("foo") { it.imports(barLibrary) }
        }


        @Test
        fun `Implementation should depend on API dependencies from imported library`() {
            assert(project.configurations, "configurations")
                    .containsItem("fooImplementation") {
                        it.extendsFrom("barApi")
                    }
        }


        @Test
        fun `Implementation should depend on classes from imported library`() {
            assert(project.configurations, "configurations")
                    .containsItem("fooImplementation") {
                        it.containsDependency<FileCollectionDependency>("bar's output") {
                            it.files == barLibrary.sourceSet.output
                        }
                    }
        }
    }
}
