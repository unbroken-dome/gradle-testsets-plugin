package org.unbrokendome.gradle.plugins.testsets.dsl

import groovy.lang.Closure
import groovy.lang.DelegatesTo
import org.gradle.api.Action
import org.gradle.api.Named
import org.gradle.api.PolymorphicDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.internal.AbstractPolymorphicDomainObjectContainer
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


    operator fun String.invoke(configureAction: TestSet.() -> Unit = {}): TestSet =
            maybeCreate(this, TestSet::class.java).also(configureAction)


    operator fun <T : TestSetBase> String.invoke(type: KClass<T>, configureAction: T.() -> Unit = {}): T =
            maybeCreate(this, type.java).also(configureAction)


    fun TestSetBase.imports(vararg libraryNames: String) {
        imports(*names.map { getByName(it) as TestLibrary }.toTypedArray())
    }


    fun TestSetBase.extendsFrom(vararg testSetNames: String) {
        extendsFrom(*names.map { getByName(it) }.toTypedArray())
    }
}


private open class DefaultTestSetContainer
@Inject constructor(project: Project, instantiator: Instantiator)
    : AbstractPolymorphicDomainObjectContainer<TestSetBase>(
        TestSetBase::class.java,
        instantiator,
        Named.Namer.INSTANCE),
        TestSetContainer {

    private companion object {
        val CREATEABLE_TYPES: Set<Class<out TestSetBase>> = setOf(
                TestSet::class.java, TestLibrary::class.java)
    }


    private val unitTestSet = PredefinedUnitTestSet(project.sourceSets[SourceSet.TEST_SOURCE_SET_NAME])
            .also { add(it) }


    override fun create(name: String): TestSet =
            create(name, TestSet::class.java)


    override fun create(name: String, configureClosure: Closure<Any>): TestSet =
            create(name, TestSet::class.java, configureClosure.toAction())


    private val entityInstantiator = object : NamedEntityInstantiator<TestSetBase> {
        @Suppress("UNCHECKED_CAST")
        override fun <S : TestSetBase> create(name: String, type: Class<S>): S {
            val sourceSet = project.sourceSets.create(NamingConventions.sourceSetName(name))
            return when (type) {
                TestSet::class.java ->
                    project.objects.newTestSet(name, sourceSet)
                            .apply { extendsFrom(unitTestSet) }
                TestLibrary::class.java ->
                    project.objects.newTestLibrary(name, sourceSet)
                else ->
                    throw IllegalArgumentException("Cannot instantiate $type")
            } as S
        }
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
