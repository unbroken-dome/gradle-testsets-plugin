package org.unbrokendome.gradle.plugins.testsets.dsl

import groovy.lang.Closure
import groovy.lang.DelegatesTo
import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.PolymorphicDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.internal.DefaultPolymorphicDomainObjectContainer
import org.gradle.api.tasks.SourceSet
import org.gradle.internal.reflect.Instantiator
import org.gradle.model.internal.core.NamedEntityInstantiator
import org.unbrokendome.gradle.plugins.testsets.util.get
import org.unbrokendome.gradle.plugins.testsets.util.sourceSets
import org.unbrokendome.gradle.plugins.testsets.util.toAction
import javax.inject.Inject
import kotlin.reflect.KClass


interface TestSetContainer : PolymorphicDomainObjectContainer<TestSetBase> {

    @JvmDefault
    override fun create(name: String): TestSet =
            create(name, TestSet::class.java)

    @JvmDefault
    fun createTestSet(name: String, configureAction: Action<TestSet>): TestSet =
        create(name, TestSet::class.java, configureAction)

    @JvmDefault
    override fun create(name: String,
                        @DelegatesTo(TestSet::class, strategy = Closure.DELEGATE_FIRST)
                        configureClosure: Closure<Any>): TestSet =
            create(name, TestSet::class.java, configureClosure.toAction())

    @JvmDefault
    fun createLibrary(name: String): TestLibrary =
            create(name, TestLibrary::class.java)

    @JvmDefault
    fun createLibrary(name: String, configureAction: Action<TestLibrary>): TestLibrary =
            create(name, TestLibrary::class.java, configureAction)

    @JvmDefault
    fun createLibrary(name: String,
                      @DelegatesTo(TestLibrary::class, strategy = Closure.DELEGATE_FIRST)
                      configureClosure: Closure<*>): TestLibrary =
            createLibrary(name, configureClosure.toAction())


    @JvmDefault
    val libraries: NamedDomainObjectContainer<TestLibrary>
        get() = containerWithType(TestLibrary::class.java)


    @JvmDefault
    fun libraries(action: Action<NamedDomainObjectContainer<TestLibrary>>) =
            action.execute(libraries)


    operator fun String.invoke(configureAction: TestSet.() -> Unit = {}): TestSet =
            maybeCreate(this, TestSet::class.java).also(configureAction)


    operator fun <T : TestSetBase> String.invoke(type: KClass<T>, configureAction: T.() -> Unit = {}): T =
            maybeCreate(this, type.java).also(configureAction)
}


@Suppress("DEPRECATION")
private open class DefaultTestSetContainer
@Inject constructor(private val project: Project, instantiator: Instantiator)
// Use this constructor, as they are still supporting it because 'nebula.lint' uses it
    : DefaultPolymorphicDomainObjectContainer<TestSetBase>(
        TestSetBase::class.java,
        instantiator),
        TestSetContainer {

    private companion object {
        val CREATEABLE_TYPES: Set<Class<out TestSetBase>> = setOf(
                TestSet::class.java, TestLibrary::class.java)
    }


    @Suppress("LeakingThis")
    private val unitTestSet = PredefinedUnitTestSet(this,
            project.sourceSets[SourceSet.TEST_SOURCE_SET_NAME])
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
                                project.configurations[sourceSet.compileClasspathConfigurationName])
                        sourceSet.runtimeClasspath = project.files(
                                sourceSet.output,
                                project.sourceSets["main"].output,
                                project.configurations[sourceSet.runtimeClasspathConfigurationName])
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
