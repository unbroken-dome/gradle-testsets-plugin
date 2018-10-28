package org.unbrokendome.gradle.plugins.testsets.dsl

import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.SourceSet
import javax.inject.Inject


interface TestSet : TestSetBase {

    val testTaskName: String
        get() = NamingConventions.testTaskName(name)
}


private open class DefaultTestSet
@Inject constructor(name: String, sourceSet: SourceSet)
    : AbstractTestSetBase(name, sourceSet), TestSet


internal fun ObjectFactory.newTestSet(name: String, sourceSet: SourceSet): TestSet =
        newInstance(DefaultTestSet::class.java, name, sourceSet)
