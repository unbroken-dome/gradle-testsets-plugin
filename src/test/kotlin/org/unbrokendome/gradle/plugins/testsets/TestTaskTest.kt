package org.unbrokendome.gradle.plugins.testsets

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.prop
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Test
import org.unbrokendome.gradle.plugins.testsets.dsl.testSets
import org.unbrokendome.gradle.plugins.testsets.testutils.containsItem
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

        assertThat(project.tasks, "tasks")
                .containsItem("fooTest") {
                    it.isInstanceOf(TestTask::class).let {
                        it.prop("testClassesDirs", TestTask::getTestClassesDirs)
                                .isEqualTo(testSet.sourceSet.output.classesDirs)
                        it.prop("classpath", TestTask::getClasspath)
                                .isEqualTo(testSet.sourceSet.runtimeClasspath)
                    }
                }
    }
}
