package org.unbrokendome.gradle.plugins.testsets

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.prop
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.PublishArtifact
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.api.tasks.bundling.Jar
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Test
import org.unbrokendome.gradle.plugins.testsets.dsl.testSets
import org.unbrokendome.gradle.plugins.testsets.testutils.assertions.containsItem
import org.unbrokendome.gradle.plugins.testsets.testutils.assertions.containsTask
import org.unbrokendome.gradle.plugins.testsets.testutils.assertions.hasSingleItem


class ArtifactTest {

    private val project: Project = ProjectBuilder.builder().build()
        .also {
            it.plugins.apply(TestSetsPlugin::class.java)
        }


    @Test
    fun `Should create JAR task for test set`() {
        project.testSets.create("foo") {
            it.createArtifact = true
            it.classifier = "foo-classifier"
        }

        assertThat(this::project)
            .containsTask<Jar>("fooJar")
            .prop("classifier") { it.archiveClassifier.get() }
            .isEqualTo("foo-classifier")
    }


    @Test
    fun `Should create artifact for test set`() {
        project.testSets.create("foo") {
            it.createArtifact = true
            it.classifier = "foo-classifier"
        }

        project.evaluate()

        assertThat(project.configurations, "configurations")
            .containsItem("foo")
            .prop("allArtifacts", Configuration::getAllArtifacts)
            .hasSingleItem()
            .prop("classifier", PublishArtifact::getClassifier)
            .isEqualTo("foo-classifier")
    }


    private fun Project.evaluate() {
        (this as ProjectInternal).evaluate()
    }
}
