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

    /**
     * The environment for the test process.
     *
     * This will be an empty [Map] if there are no environment variables.
     *
     * @see org.gradle.api.tasks.testing.Test.getEnvironment
     * @see org.gradle.api.tasks.testing.Test.setEnvironment
     */
    var environment: Map<String, Any>

    /**
     * Adds some environment variables to the environment for the test process.
     *
     * @param environmentVariables the environment variables
     *
     * @see org.gradle.api.tasks.testing.Test.environment
     */
    fun environment(environmentVariables: Map<String, Any>) {
        this.environment += environmentVariables
    }

    /**
     * Adds an environment variable to the environment for the test process.
     *
     * @param name the name of the variable
     * @param value the value for the variable
     *
     * @see org.gradle.api.tasks.testing.Test.environment
     */
    fun environment(name: String, value: Any) {
        this.environment += name to value
    }

    /**
     * The system properties that will be used for the test process.
     *
     * This will be an empty [Map] if there are no system properties.
     *
     * @see org.gradle.api.tasks.testing.Test.getSystemProperties
     * @see org.gradle.api.tasks.testing.Test.setSystemProperties
     */
    var systemProperties: Map<String, Any?>

    /**
     * Adds some system properties to use for the process.
     *
     * @param properties the system properties
     * @see org.gradle.api.tasks.testing.Test.systemProperties
     */
    fun systemProperties(properties: Map<String, *>) {
        this.systemProperties += properties
    }

    /**
     * Adds a system property to use for the process.
     *
     * @param name The name of the property
     * @param value The value for the property. May be null.
     * @return this
     */
    fun systemProperty(name: String, value: Any?) {
        this.systemProperties += name to value
    }
}


private open class DefaultTestSet
@Inject constructor(container: TestSetContainer, name: String, sourceSet: SourceSet)
    : AbstractTestSetBase(container, name, sourceSet), TestSet {

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


internal fun ObjectFactory.newTestSet(container: TestSetContainer, name: String, sourceSet: SourceSet): TestSet =
        newInstance(DefaultTestSet::class.java, container, name, sourceSet)
