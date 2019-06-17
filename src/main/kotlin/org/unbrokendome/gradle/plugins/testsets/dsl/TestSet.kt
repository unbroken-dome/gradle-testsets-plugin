package org.unbrokendome.gradle.plugins.testsets.dsl

import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.SourceSet
import javax.inject.Inject


interface TestSet : TestSetBase {

    val testTaskName: String
        get() = NamingConventions.testTaskName(name)

    /**
     * The name of the [JacocoReport][org.gradle.testing.jacoco.tasks.JacocoReport] task creating the JaCoCo reports
     * for this test set.
     *
     * Only relevant if the `jacoco` Gradle plugin is also applied to the project.
     */
    val jacocoReportTaskName: String
        get() = NamingConventions.jacocoReportTaskName(testTaskName)
}


private open class DefaultTestSet
@Inject constructor(container: TestSetContainer, name: String, sourceSet: SourceSet)
    : AbstractTestSetBase(container, name, sourceSet), TestSet


internal fun ObjectFactory.newTestSet(container: TestSetContainer, name: String, sourceSet: SourceSet): TestSet =
        newInstance(DefaultTestSet::class.java, container, name, sourceSet)
