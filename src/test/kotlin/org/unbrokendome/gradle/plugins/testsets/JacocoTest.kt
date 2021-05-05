package org.unbrokendome.gradle.plugins.testsets

import assertk.all
import assertk.assertThat
import assertk.assertions.containsOnly
import assertk.assertions.prop
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.testing.jacoco.plugins.JacocoPlugin
import org.gradle.testing.jacoco.plugins.JacocoTaskExtension
import org.gradle.testing.jacoco.tasks.JacocoReport
import org.junit.jupiter.api.Test
import org.unbrokendome.gradle.plugins.testsets.dsl.testSets
import org.unbrokendome.gradle.plugins.testsets.testutils.assertions.containsTask
import org.unbrokendome.gradle.plugins.testsets.testutils.assertions.hasExtension
import org.gradle.api.tasks.testing.Test as TestTask

class JacocoTest {

    private val project: Project = ProjectBuilder.builder().build()
        .also { project ->
            project.plugins.apply(TestSetsPlugin::class.java)
            project.plugins.apply(JacocoPlugin::class.java)
        }


    @Test
    fun `should create JacocoReport task for test set`() {
        project.testSets.create("foo")

        assertThat(this::project).all {

            containsTask<TestTask>("foo")
                .hasExtension<JacocoTaskExtension>()
            val jacocoTaskExtension = project.tasks.getByName("foo")
                .extensions.getByType(JacocoTaskExtension::class.java)

            containsTask<JacocoReport>("jacocoFooReport")
                .all {
                    prop("executionData") { it.executionData }
                        .transform { it.toSet() }
                        .containsOnly(jacocoTaskExtension.destinationFile)
                    prop("allSourceDirs", JacocoReport::getAllSourceDirs)
                        .transform { it.toSet() }
                        .containsOnly(project.file("src/main/java"))
                    prop("classDirectories", JacocoReport::getClassDirectories)
                        .transform { it.toSet() }
                        .containsOnly(
                            project.file("build/classes/java/main"),
                            project.file("build/resources/main")
                        )
                }
        }
    }


}
