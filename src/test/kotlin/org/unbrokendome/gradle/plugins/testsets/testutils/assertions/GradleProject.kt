package org.unbrokendome.gradle.plugins.testsets.testutils.assertions

import assertk.Assert
import assertk.assertions.support.expected
import assertk.assertions.support.show
import org.gradle.api.Project
import org.gradle.api.Task


inline fun <reified T : Task> Assert<Project>.containsTask(taskName: String) =
    transform("task \"$taskName\"") { actual ->
        val task = actual.tasks.findByName(taskName)
            ?: expected("to contain a task named \"$taskName\"")
        (task as? T) ?: expected("to contain a task named \"$taskName\" of type ${show(T::class)}")
    }
