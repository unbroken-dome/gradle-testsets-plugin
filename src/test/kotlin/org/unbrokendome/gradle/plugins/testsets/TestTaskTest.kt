package org.unbrokendome.gradle.plugins.testsets

import assertk.all
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.prop
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Test
import org.unbrokendome.gradle.plugins.testsets.dsl.testSets
import org.unbrokendome.gradle.plugins.testsets.testutils.assertions.containsTask
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

        assertThat(this::project)
            .containsTask<TestTask>("fooTest")
            .all {
                prop("testClassesDirs", TestTask::getTestClassesDirs)
                    .isEqualTo(testSet.sourceSet.output.classesDirs)
                prop("classpath", TestTask::getClasspath)
                    .isEqualTo(testSet.sourceSet.runtimeClasspath)
            }
    }
}
