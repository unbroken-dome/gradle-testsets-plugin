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
@Inject constructor(container: TestSetContainer, name: String, sourceSet: SourceSet)
    : AbstractTestSetBase(container, name, sourceSet), TestLibrary


internal fun ObjectFactory.newTestLibrary(container: TestSetContainer, name: String, sourceSet: SourceSet): TestLibrary =
        newInstance(DefaultTestLibrary::class.java, container, name, sourceSet)
