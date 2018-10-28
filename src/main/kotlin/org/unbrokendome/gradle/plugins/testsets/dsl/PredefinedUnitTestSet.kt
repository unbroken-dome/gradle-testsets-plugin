package org.unbrokendome.gradle.plugins.testsets.dsl

import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.SourceSet


internal class PredefinedUnitTestSet(sourceSet: SourceSet)
    : AbstractTestSetBase("unitTest", sourceSet), TestSet {

    override val testTaskName: String
        get() = JavaPlugin.TEST_TASK_NAME

    override val jarTaskName: String
        get() = "testJar"

    override var classifier: String = "tests"
}
