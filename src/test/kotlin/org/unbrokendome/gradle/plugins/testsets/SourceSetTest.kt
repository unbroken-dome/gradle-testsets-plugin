package org.unbrokendome.gradle.plugins.testsets

import assertk.all
import assertk.assertThat
import assertk.assertions.containsOnly
import assertk.assertions.prop
import org.gradle.api.Project
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.plugins.GroovyPlugin
import org.gradle.api.tasks.GroovySourceDirectorySet
import org.gradle.api.tasks.SourceSet
import org.gradle.testfixtures.ProjectBuilder
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformJvmPlugin
import org.junit.jupiter.api.Test
import org.unbrokendome.gradle.plugins.testsets.dsl.testSets
import org.unbrokendome.gradle.plugins.testsets.testutils.assertions.containsItem
import org.unbrokendome.gradle.plugins.testsets.testutils.assertions.hasExtension
import org.unbrokendome.gradle.plugins.testsets.util.sourceSets


class SourceSetTest {

    private val project: Project = ProjectBuilder.builder().build()
        .also {
            it.plugins.apply(TestSetsPlugin::class.java)
        }


    @Test
    fun `Should create a new source set for a test set`() {
        project.testSets.create("foo")

        assertThat(project.sourceSets, "sourceSets")
            .containsItem("foo")
    }


    @Test
    fun `Changing dirName should be reflected in source set`() {
        project.testSets.create("foo") {
            it.dirName = "bar"
        }

        assertThat(project.sourceSets, "sourceSets")
            .containsItem("foo")
            .all {
                prop("java", SourceSet::getJava)
                    .prop("srcDirs", SourceDirectorySet::getSrcDirs)
                    .containsOnly(project.file("src/bar/java"))
                prop("resources", SourceSet::getResources)
                    .prop("srcDirs", SourceDirectorySet::getSrcDirs)
                    .containsOnly(project.file("src/bar/resources"))
            }
    }


    @Test
    fun `Changing dirName should be reflected in Groovy source directories`() {
        project.plugins.apply(GroovyPlugin::class.java)

        project.testSets.create("foo") {
            it.dirName = "bar"
        }

        assertThat(project.sourceSets, "sourceSets")
            .containsItem("foo")
            .hasExtension<GroovySourceDirectorySet>("groovy")
            .prop("srcDirs", SourceDirectorySet::getSrcDirs)
            .containsOnly(project.file("src/bar/groovy"))
    }


    @Test
    fun `Changing dirName should be reflected in Kotlin source directories`() {
        project.plugins.apply("org.jetbrains.kotlin.jvm")

        project.testSets.create("foo") {
            it.dirName = "bar"
        }

        assertThat(project.sourceSets, "sourceSets")
            .containsItem("foo")
            .hasExtension<SourceDirectorySet>("kotlin")
            .prop("srcDirs", SourceDirectorySet::getSrcDirs)
            .containsOnly(
                project.file("src/bar/kotlin"),
                project.file("src/bar/java")
            )
    }
}
