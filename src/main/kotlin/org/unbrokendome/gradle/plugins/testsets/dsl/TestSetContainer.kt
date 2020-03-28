package org.unbrokendome.gradle.plugins.testsets.dsl

import groovy.lang.Closure
import groovy.lang.DelegatesTo
import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.PolymorphicDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.internal.CollectionCallbackActionDecorator
import org.gradle.api.internal.DefaultPolymorphicDomainObjectContainer
import org.gradle.api.tasks.SourceSet
import org.gradle.internal.reflect.Instantiator
import org.gradle.model.internal.core.NamedEntityInstantiator
import org.unbrokendome.gradle.plugins.testsets.util.get
import org.unbrokendome.gradle.plugins.testsets.util.sourceSets
import org.unbrokendome.gradle.plugins.testsets.util.toAction
import javax.inject.Inject
import kotlin.reflect.KClass


/**
 * A container for test sets and test libraries.
 */
interface TestSetContainer : PolymorphicDomainObjectContainer<TestSetBase> {

    /**
     * Creates a new test set with the specified name, and adds it to the container.
     *
     * @param name the name of the test set
     * @return the new [TestSet]
     */
    @JvmDefault
    override fun create(name: String): TestSet =
        create(name, TestSet::class.java)


    /**
     * Creates a new test set with the specified name, adds it to the container, and configures it
     * with the specified action.
     *
     * @param name the name of the test set
     * @param configureAction an action for configuring the test set
     * @return the new [TestSet]
     */
    @JvmDefault
    fun createTestSet(name: String, configureAction: Action<TestSet>): TestSet =
        create(name, TestSet::class.java, configureAction)


    /**
     * Creates a new test set with the specified name, adds it to the container, and configures it
     * with the specified closure.
     *
     * This variant is intended for Groovy DSL support, with an annotated closure parameter for better
     * IDE support.
     *
     * @param name the name of the test set
     * @param configureClosure a closure for configuring the test set
     * @return the new [TestSet]
     */
    @JvmDefault
    override fun create(
        name: String,
        @DelegatesTo(TestSet::class, strategy = Closure.DELEGATE_FIRST)
        configureClosure: Closure<Any>
    ): TestSet =
        create(name, TestSet::class.java, configureClosure.toAction())


    /**
     * Creates a new test library with the specified name, and adds it to the container.
     *
     * @param name the name of the test library
     * @return the new [TestLibrary]
     */
    @JvmDefault
    fun createLibrary(name: String): TestLibrary =
        create(name, TestLibrary::class.java)


    /**
     * Creates a new test library with the specified name, adds it to the container, and configures it with
     * the specified action.
     *
     * @param name the name of the test library
     * @param configureAction an action for configuring the test library
     * @return the new [TestLibrary]
     */
    @JvmDefault
    fun createLibrary(name: String, configureAction: Action<TestLibrary>): TestLibrary =
        create(name, TestLibrary::class.java, configureAction)


    /**
     * Creates a new test library with the specified name, adds it to the container, and configures it with
     * the specified closure.
     *
     * This variant is intended for Groovy DSL support, with an annotated closure parameter for better
     * IDE support.
     *
     * @param name the name of the test library
     * @param configureClosure an closure for configuring the test library
     * @return the new [TestLibrary]
     */
    @JvmDefault
    fun createLibrary(
        name: String,
        @DelegatesTo(TestLibrary::class, strategy = Closure.DELEGATE_FIRST)
        configureClosure: Closure<*>
    ): TestLibrary =
        createLibrary(name, configureClosure.toAction())


    /**
     * A [NamedDomainObjectContainer] that wraps this container but presents only the test libraries.
     */
    @JvmDefault
    val libraries: NamedDomainObjectContainer<TestLibrary>
        get() = containerWithType(TestLibrary::class.java)


    /**
     * Configures the test libraries with the specified action.
     *
     * @param action an action that configures the [NamedDomainObjectContainer] presenting only the test libraries
     */
    @JvmDefault
    fun libraries(action: Action<NamedDomainObjectContainer<TestLibrary>>) =
        action.execute(libraries)


    /**
     * Configures the test set with the specified name, creating it if it does not exist.
     *
     * Intended for Kotlin DSL support (in Groovy DSL, containers will automatically support domain object names
     * as member functions)
     *
     * @receiver the name of the test set
     * @param configureAction an action for configuring the [TestSet]
     * @return the [TestSet]
     */
    operator fun String.invoke(configureAction: TestSet.() -> Unit = {}): TestSet =
        maybeCreate(this, TestSet::class.java).also(configureAction)


    /**
     * Configures the test set or test library with the specified name, creating it if it does not exist.
     *
     * Intended for Kotlin DSL support (in Groovy DSL, containers will automatically support domain object names
     * as member functions)
     *
     * @param type the type of object as a Kotlin [KClass] (either `TestSet::class` or `TestLibrary::class`)
     * @return the [TestSet] or [TestLibrary]
     * @throws ClassCastException if the item already exists in the container, but with a different type
     */
    operator fun <T : TestSetBase> String.invoke(type: KClass<T>, configureAction: T.() -> Unit = {}): T =
        maybeCreate(this, type.java).also(configureAction)
}


private open class DefaultTestSetContainer
@Inject constructor(
    private val project: Project,
    instantiator: Instantiator
) : DefaultPolymorphicDomainObjectContainer<TestSetBase>(
    TestSetBase::class.java,
    instantiator,
    CollectionCallbackActionDecorator.NOOP
),
    TestSetContainer {

    private companion object {
        val CREATEABLE_TYPES: Set<Class<out TestSetBase>> = setOf(
            TestSet::class.java, TestLibrary::class.java
        )
    }


    @Suppress("LeakingThis")
    private val unitTestSet = PredefinedUnitTestSet(
        this,
        project.sourceSets[SourceSet.TEST_SOURCE_SET_NAME]
    )
        .also { add(it) }


    override fun create(name: String): TestSet =
        create(name, TestSet::class.java)


    override fun create(name: String, configureClosure: Closure<Any>): TestSet =
        create(name, TestSet::class.java, configureClosure.toAction())


    private val entityInstantiator = object : NamedEntityInstantiator<TestSetBase> {
        @Suppress("UNCHECKED_CAST")
        override fun <S : TestSetBase> create(name: String, type: Class<S>): S {
            val sourceSet = createSourceSetForTestSet(name)
            return when (type) {
                TestSet::class.java ->
                    project.objects.newTestSet(this@DefaultTestSetContainer, name, sourceSet)
                        .apply { extendsFrom(unitTestSet) }
                TestLibrary::class.java ->
                    project.objects.newTestLibrary(this@DefaultTestSetContainer, name, sourceSet)
                else ->
                    throw IllegalArgumentException("Cannot instantiate $type")
            } as S
        }
    }


    /**
     * Creates a new [SourceSet] for a test set.
     *
     * This sets up the new source set's compile classpath and runtime classpath, analogously to what the
     * JavaPlugin does with the `test` sourceSet. The reference to the `main` output is not modeled as a dependency
     * but is set directly on the source set's classpath. That's why we won't inherit this by extending from the
     * `unitTest` set, so we have to configure it for each new test set.
     *
     * @param name the name of the new test set
     */
    private fun createSourceSetForTestSet(name: String): SourceSet =
        project.sourceSets.create(NamingConventions.sourceSetName(name))
            .also { sourceSet ->
                sourceSet.compileClasspath = project.files(
                    project.sourceSets["main"].output,
                    project.configurations[sourceSet.compileClasspathConfigurationName]
                )
                sourceSet.runtimeClasspath = project.files(
                    sourceSet.output,
                    project.sourceSets["main"].output,
                    project.configurations[sourceSet.runtimeClasspathConfigurationName]
                )
            }


    override fun doCreate(name: String): TestSet =
        doCreate(name, TestSet::class.java)


    override fun <U : TestSetBase> doCreate(name: String, type: Class<U>): U =
        entityInstantiator.create(name, type)


    override fun getCreateableTypes(): Set<Class<out TestSetBase>> =
        CREATEABLE_TYPES


    override fun getEntityInstantiator(): NamedEntityInstantiator<TestSetBase> =
        entityInstantiator
}


internal fun Project.testSetContainer(instantiator: Instantiator): TestSetContainer =
    objects.newInstance(DefaultTestSetContainer::class.java, this, instantiator)
