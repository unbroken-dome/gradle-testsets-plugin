package org.unbrokendome.gradle.plugins.testsets.dsl

import org.gradle.api.Named
import org.gradle.api.tasks.SourceSet
import org.unbrokendome.gradle.plugins.testsets.util.observableSetOf


interface TestSetBase : Named {

    /**
     * The source set connected to this test set.
     */
    val sourceSet: SourceSet

    /**
     * The directory name for the source set. For example, if this property is set to `"my-test"`, the
     * Java sources will be located at `src/my-test/java`.
     *
     * Defaults to the [name][getName] of the test set.
     */
    var dirName: String

    /**
     * Other test sets or libraries that this test set extends from, inheriting their dependencies in the respective
     * configurations.
     */
    var extendsFrom: MutableSet<TestSetBase>

    /**
     * Adds the given test sets or libraries to the set of extended test sets.
     *
     * @param testSets the other test sets or libraries to extend from
     */
    @JvmDefault
    fun extendsFrom(vararg testSets: TestSetBase) =
            extendsFrom.addAll(testSets)

    /**
     * A set of test libraries that this test set depends on.
     */
    var imports: MutableSet<TestLibrary>

    /**
     * Adds the given libraries to the set of imported libraries for this test set.
     *
     * @param libraries the libraries to import
     */
    @JvmDefault
    fun imports(vararg libraries: TestLibrary) =
            imports.addAll(libraries)

    /**
     * The name of the source set containing the sources for this test set.
     */
    val sourceSetName: String
        get() = sourceSet.name

    /**
     * If `true`, an artifact for publishing this test set will be added to the project.
     *
     * Defaults to `false`.
     */
    var createArtifact: Boolean

    /**
     * The classifier for the JAR that is created for this test set.
     *
     * Only relevant if [createArtifact] is `true`. Defaults to the [name][getName] of the test set.
     */
    var classifier: String

    /**
     * The name of the configuration containing the compile dependencies for this test set.
     */
    val compileConfigurationName: String
        get() = sourceSet.compileConfigurationName

    /**
     * The name of the configuration containing the compile-only dependencies for this test set.
     */
    val compileOnlyConfigurationName: String
        get() = sourceSet.compileOnlyConfigurationName

    /**
     * The name of the configuration containing all compile classpath dependencies for this test set.
     */
    val compileClasspathConfigurationName: String
        get() = sourceSet.compileClasspathConfigurationName

    /**
     * The name of the configuration containing the annotation processor dependencies for this test set.
     */
    val annotationProcessorConfigurationName: String
        get() = sourceSet.annotationProcessorConfigurationName

    /**
     * The name of the configuration containing the implementation dependencies for this test set.
     */
    val implementationConfigurationName: String
        get() = sourceSet.implementationConfigurationName

    /**
     * The name of the configuration containing the runtime dependencies for this test set.
     */
    val runtimeConfigurationName: String
        get() = sourceSet.runtimeConfigurationName

    /**
     * The name of the configuration containing the runtime-only dependencies for this test set.
     */
    val runtimeOnlyConfigurationName: String
        get() = sourceSet.runtimeOnlyConfigurationName

    /**
     * The name of the configuration containing all runtime classpath dependencies for this test set.
     */
    val runtimeClasspathConfigurationName: String
        get() = sourceSet.runtimeClasspathConfigurationName

    /**
     * The name of the configuration that can be consumed by other projects importing this test set.
     */
    val runtimeElementsConfigurationName: String
        get() = sourceSet.runtimeElementsConfigurationName

    /**
     * The name of the [Jar][org.gradle.api.tasks.bundling.Jar] task building the JAR for this test set.
     *
     * Only relevant if [createArtifact] is `true`.
     */
    val jarTaskName: String
        get() = NamingConventions.jarTaskName(name)

    /**
     * The name of the outbound configuration containing the artifact for this test set.
     *
     * Only relevant if [createArtifact] is `true`.
     */
    val artifactConfigurationName: String
        get() = NamingConventions.artifactConfigurationName(name)
}


internal interface TestSetObserver {

    fun dirNameChanged(testSet: TestSetBase, oldDirName: String, newDirName: String) {}

    fun extendsFromAdded(testSet: TestSetBase, added: TestSetBase) {}

    fun extendsFromRemoved(testSet: TestSetBase, removed: TestSetBase) {}

    fun importAdded(testSet: TestSetBase, added: TestLibrary) {}

    fun importRemoved(testSet: TestSetBase, removed: TestLibrary) {}
}


internal interface TestSetBaseInternal : TestSetBase {

    fun addObserver(observer: TestSetObserver, notifyExisting: Boolean = true)


    @JvmDefault
    fun addObservers(observers: Iterable<TestSetObserver>, notifyExisting: Boolean = true) =
            observers.forEach { addObserver(it, notifyExisting) }


    @JvmDefault
    fun addObservers(vararg observers: TestSetObserver, notifyExisting: Boolean = true) =
            observers.forEach { addObserver(it, notifyExisting) }
}


internal abstract class AbstractTestSetBase(
        private val name: String,
        override val sourceSet: SourceSet)
    : TestSetBase, TestSetBaseInternal {

    private val observers = mutableListOf<TestSetObserver>()


    override fun getName(): String =
            name


    override var dirName: String = name
        set(value) {
            if (field != value) {
                val oldValue = field
                field = value
                notifyObservers { it.dirNameChanged(this, oldValue, value) }
            }
        }


    override var extendsFrom: MutableSet<TestSetBase> = observableSetOf(
            elementAdded = { element -> notifyObservers { it.extendsFromAdded(this, element) }},
            elementRemoved = { element -> notifyObservers { it.extendsFromRemoved(this, element) }})
        set(value) {
            field.clear()
            field.addAll(value)
        }


    override var imports: MutableSet<TestLibrary> = observableSetOf(
            elementAdded = { element -> notifyObservers { it.importAdded(this, element) }},
            elementRemoved = { element -> notifyObservers { it.importRemoved(this, element) }})
        set(value) {
            field.clear()
            field.addAll(value)
        }


    override var createArtifact: Boolean = false


    override var classifier: String = name


    override fun addObserver(observer: TestSetObserver, notifyExisting: Boolean) {
        observers.add(observer)
        if (notifyExisting) {
            extendsFrom.forEach {
                observer.extendsFromAdded(this, it)
            }
            imports.forEach {
                observer.importAdded(this, it)
            }
        }
    }


    private fun notifyObservers(action: (TestSetObserver) -> Unit) {
        for (observer in observers) {
            action(observer)
        }
    }
}
