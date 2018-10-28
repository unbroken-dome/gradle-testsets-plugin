package org.unbrokendome.gradle.plugins.testsets

import assertk.all
import assertk.assert
import assertk.assertions.contains
import assertk.assertions.hasSize
import assertk.assertions.prop
import org.gradle.api.Project
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.plugins.GroovyPlugin
import org.gradle.api.tasks.GroovySourceSet
import org.gradle.api.tasks.SourceSet
import org.gradle.testfixtures.ProjectBuilder
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformJvmPlugin
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.junit.jupiter.api.Test
import org.unbrokendome.gradle.plugins.testsets.dsl.testSets
import org.unbrokendome.gradle.plugins.testsets.testutils.containsItem
import org.unbrokendome.gradle.plugins.testsets.testutils.hasConvention
import org.unbrokendome.gradle.plugins.testsets.util.sourceSets

@Suppress("NestedLambdaShadowedImplicitParameter")
class SourceSetTest {

    private val project: Project = ProjectBuilder.builder().build()
            .also {
                it.plugins.apply(TestSetsPlugin::class.java)
            }


    @Test
    fun `Should create a new source set for a test set`() {
        project.testSets.create("foo")

        assert(project.sourceSets, "sourceSets")
                .containsItem("foo")
    }


    @Test
    fun `Changing dirName should be reflected in source set`() {
        project.testSets.create("foo") {
            it.dirName = "bar"
        }

        assert(project.sourceSets, "sourceSets")
                .containsItem("foo") {
                    it.prop("java", SourceSet::getJava)
                            .prop("srcDirs", SourceDirectorySet::getSrcDirs)
                            .all {
                                hasSize(1)
                                contains(project.file("src/bar/java"))
                            }
                    it.prop("resources", SourceSet::getResources)
                            .prop("srcDirs", SourceDirectorySet::getSrcDirs)
                            .all {
                                hasSize(1)
                                contains(project.file("src/bar/resources"))
                            }
                }
    }


    @Test
    fun `Changing dirName should be reflected in Groovy source directories`() {
        project.plugins.apply(GroovyPlugin::class.java)

        project.testSets.create("foo") {
            it.dirName = "bar"
        }

        assert(project.sourceSets, "sourceSets")
                .containsItem("foo") {
                    it.hasConvention<GroovySourceSet> {
                        it.prop("groovy", GroovySourceSet::getGroovy)
                                .prop("srcDirs", SourceDirectorySet::getSrcDirs)
                                .all {
                                    hasSize(1)
                                    contains(project.file("src/bar/groovy"))
                                }
                    }
                }
    }


    @Test
    fun `Changing dirName should be reflected in Kotlin source directories`() {
        project.plugins.apply(KotlinPlatformJvmPlugin::class.java)

        project.testSets.create("foo") {
            it.dirName = "bar"
        }

        assert(project.sourceSets, "sourceSets")
                .containsItem("foo") {
                    it.hasConvention<KotlinSourceSet> {
                        it.prop("kotlin") { it.kotlin }
                                .prop("srcDirs", SourceDirectorySet::getSrcDirs)
                                .all {
                                    hasSize(2)
                                    contains(project.file("src/bar/kotlin"))
                                    contains(project.file("src/bar/java"))
                                }
                    }
                }
    }
}
