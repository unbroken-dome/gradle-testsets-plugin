package org.unbrokendome.gradle.plugins.testsets.dsl

import org.gradle.api.Project
import org.unbrokendome.gradle.plugins.testsets.util.extension


val Project.testSets: TestSetContainer
    get() = extension()


fun Project.testSets(action: TestSetContainer.() -> Unit) {
    with(testSets, action)
}
