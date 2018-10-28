package org.unbrokendome.gradle.plugins.testsets

import assertk.assert
import assertk.assertions.isInstanceOf
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Test
import org.unbrokendome.gradle.plugins.testsets.dsl.TestSet
import org.unbrokendome.gradle.plugins.testsets.dsl.TestSetContainer
import org.unbrokendome.gradle.plugins.testsets.dsl.testSets
import org.unbrokendome.gradle.plugins.testsets.testutils.containsItem
import org.unbrokendome.gradle.plugins.testsets.testutils.hasExtension


class TestSetsPluginTest {

    private val project: Project = ProjectBuilder.builder().build()
            .also {
                it.plugins.apply(TestSetsPlugin::class.java)
            }


    @Test
    fun `should create a testSets extension`() {
        assert(project).hasExtension<TestSetContainer>("testSets")
    }


    @Test
    fun `should have a predefined "unitTest" test set`() {
        assert(project.testSets, "testSets")
                .containsItem("unitTest") {
                    it.isInstanceOf(TestSet::class)
                }
    }
}
