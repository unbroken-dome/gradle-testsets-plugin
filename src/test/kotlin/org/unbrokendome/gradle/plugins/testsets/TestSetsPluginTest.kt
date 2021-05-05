package org.unbrokendome.gradle.plugins.testsets

import assertk.assertThat
import assertk.assertions.isInstanceOf
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Test
import org.unbrokendome.gradle.plugins.testsets.dsl.TestSet
import org.unbrokendome.gradle.plugins.testsets.dsl.TestSetContainer
import org.unbrokendome.gradle.plugins.testsets.dsl.testSets
import org.unbrokendome.gradle.plugins.testsets.testutils.assertions.containsItem
import org.unbrokendome.gradle.plugins.testsets.testutils.assertions.hasExtension


class TestSetsPluginTest {

    private val project: Project = ProjectBuilder.builder().build()
        .also {
            it.plugins.apply(TestSetsPlugin::class.java)
        }


    @Test
    fun `should create a testSets extension`() {
        assertThat(this::project)
            .hasExtension<TestSetContainer>("testSets")
    }


    @Test
    fun `should have a predefined 'unitTest' test set`() {
        assertThat(project.testSets, "testSets")
            .containsItem("unitTest")
            .isInstanceOf(TestSet::class)
    }
}
