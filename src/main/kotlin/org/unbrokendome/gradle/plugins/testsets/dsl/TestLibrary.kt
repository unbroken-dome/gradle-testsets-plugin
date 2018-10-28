package org.unbrokendome.gradle.plugins.testsets.dsl

import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.SourceSet
import javax.inject.Inject


interface TestLibrary : TestSetBase {

    val apiConfigurationName: String
        get() = sourceSet.apiConfigurationName

    val apiElementsConfigurationName: String
        get() = sourceSet.apiElementsConfigurationName
}


private open class DefaultTestLibrary
@Inject constructor(name: String, sourceSet: SourceSet)
    : AbstractTestSetBase(name, sourceSet), TestLibrary


internal fun ObjectFactory.newTestLibrary(name: String, sourceSet: SourceSet): TestLibrary =
        newInstance(DefaultTestLibrary::class.java, name, sourceSet)
