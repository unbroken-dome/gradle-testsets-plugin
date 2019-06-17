package org.unbrokendome.gradle.plugins.testsets.util

import org.gradle.api.InvalidUserDataException
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.PolymorphicDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.SourceSetContainer
import kotlin.reflect.KClass


internal inline fun <reified T : Any> Project.extension(): T =
        extensions.getByType(T::class.java)


internal val Project.sourceSets: SourceSetContainer
    get() = convention.getPlugin(JavaPluginConvention::class.java).sourceSets


internal fun <T : Any> NamedDomainObjectContainer<T>.registerOrConfigure(
        name: String, configureAction: (T) -> Unit): NamedDomainObjectProvider<T> =
        try {
            register(name, configureAction)
        } catch (ex: InvalidUserDataException) {
            named(name).apply { configure(configureAction) }
        }


internal fun <T : Any, U : T> PolymorphicDomainObjectContainer<T>.registerOrConfigure(
        name: String, type: KClass<U>, configureAction: (U) -> Unit) =
    try {
        register(name, type.java, configureAction)
    } catch (ex: InvalidUserDataException) {
        named(name).also { provider ->
            provider.configure {
                check (type.isInstance(it)) {
                    "Expected item \"$name\" in $this to be of type ${type.qualifiedName}, but was ${it.javaClass.name}"
                }
                @Suppress("UNCHECKED_CAST")
                configureAction(it as U)
            }
        }
    }
