package org.unbrokendome.gradle.plugins.testsets.internal

import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.testing.Test
import org.unbrokendome.gradle.plugins.testsets.dsl.TestSet
import org.unbrokendome.gradle.plugins.testsets.dsl.TestSetBase
import org.unbrokendome.gradle.plugins.testsets.dsl.TestSetObserver


internal class TestTaskEnvironmentObserver(
    private val tasks: TaskContainer
) : TestSetObserver {

    override fun environmentVariablesChanged(testSet: TestSetBase, newEnvironment: Map<String, Any?>) {
        val testTaskName = (testSet as TestSet).testTaskName
        (tasks.findByName(testTaskName) as? Test?)?.let { task ->
            task.environment(newEnvironment)
        }
    }
}
