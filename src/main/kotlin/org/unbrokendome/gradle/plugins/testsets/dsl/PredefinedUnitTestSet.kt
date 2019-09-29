package org.unbrokendome.gradle.plugins.testsets.dsl

import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.SourceSet
import org.gradle.testing.jacoco.plugins.JacocoPlugin


internal class PredefinedUnitTestSet(container: TestSetContainer, sourceSet: SourceSet)
    : AbstractTestSetBase(container, "unitTest", sourceSet), TestSet {

    override val testTaskName: String
        get() = JavaPlugin.TEST_TASK_NAME


    override val jarTaskName: String
        get() = "testJar"


    override var classifier: String = "tests"


    override var environment: Map<String, Any> = mutableMapOf()
        set(value) {
            field = value
            notifyObservers { it.environmentVariablesChanged(this, value) }
        }


    override var systemProperties: Map<String, Any?> = mutableMapOf()
        set(value) {
            field = value
            notifyObservers { it.systemPropertiesChanged(this, value) }
        }
}
