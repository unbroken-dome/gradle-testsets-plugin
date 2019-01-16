package org.unbrokendome.gradle.plugins.testsets

import assertk.all
import assertk.assert
import assertk.assertions.contains
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.prop
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Test
import org.unbrokendome.gradle.plugins.testsets.dsl.testSets
import org.unbrokendome.gradle.plugins.testsets.testutils.containsAll
import org.unbrokendome.gradle.plugins.testsets.testutils.containsItem
import org.unbrokendome.gradle.plugins.testsets.testutils.startsWith
import org.unbrokendome.gradle.plugins.testsets.util.get
import org.unbrokendome.gradle.plugins.testsets.util.sourceSets
import org.gradle.api.tasks.testing.Test as TestTask


@Suppress("NestedLambdaShadowedImplicitParameter")
class TestTaskTest {

    private val project: Project = ProjectBuilder.builder().build()
            .also {
                it.plugins.apply(TestSetsPlugin::class.java)
            }


    @Test
    fun `Should create a Test task for each test set`() {
        val testSet = project.testSets.create("fooTest")

        assert(project.tasks, "tasks")
                .containsItem("fooTest") {
                    it.isInstanceOf(TestTask::class) {
                        it.prop("testClassesDirs", TestTask::getTestClassesDirs)
                                .isEqualTo(testSet.sourceSet.output.classesDirs)
                        it.prop("classpath", TestTask::getClasspath).all {
                            startsWith(testSet.sourceSet.runtimeClasspath)
                            containsAll(project.sourceSets["test"].output)
                        }
                    }
                }
    }
}
