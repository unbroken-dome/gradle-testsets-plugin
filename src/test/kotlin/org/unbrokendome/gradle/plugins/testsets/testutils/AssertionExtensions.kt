package org.unbrokendome.gradle.plugins.testsets.testutils

import assertk.Assert
import assertk.all
import assertk.assertions.support.expected
import assertk.assertions.support.show
import org.gradle.api.NamedDomainObjectCollection
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.Dependency
import org.gradle.api.internal.HasConvention
import org.gradle.api.plugins.ExtensionAware
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException
import assertk.assertions.isInstanceOf as defaultIsInstanceOf


inline fun <reified E : Any> Assert<*>.hasExtension(name: String? = null, noinline block: (Assert<E>) -> Unit = {}) {
    if (actual !is ExtensionAware) {
        return expected("to be ExtensionAware")
    }
    val extensions = (actual as ExtensionAware).extensions

    val extension: E = if (name != null) {
        extensions.findByName(name)
                .let {
                    if (it == null) {
                        return expected("to have an extension named \"$name\" of type ${show(E::class)}")
                    }
                    if (it !is E) {
                        return expected("to have an extension named \"$name\" of type ${show(E::class)}, but actual type was: ${show(it.javaClass)}")
                    }
                    it
                }
    } else {
        extensions.findByType(E::class.java)
                .let {
                    if (it == null) {
                        return expected("to have an extension of type ${show(E::class)}")
                    }
                    it
                }
    } as E

    assert(extension, name = "extension " + (name?.let { "\"$it\""} ?: show(E::class))).all(block)
}


inline fun <reified E : Any> Assert<*>.hasConvention(noinline block: (Assert<E>) -> Unit = {}) {
    if (actual !is HasConvention) {
        return expected("to have conventions")
    }
    val convention = (actual as HasConvention).convention

    val conventionObject: E =
            try {
                convention.getPlugin(E::class.java)
            } catch (ex: IllegalStateException) {
                return expected("to have a convention of type ${show(E::class)}")
            }

    assert(conventionObject, name = "convention object ${show(E::class)}").all(block)
}



fun <T : Any> Assert<NamedDomainObjectCollection<T>>.containsItem(name: String, block: (Assert<T>) -> Unit = {}) {
    val item = actual.findByName(name) ?: return expected("to contain an item named \"$name\"", actual = actual.toList())
    assert(item, name = name).all(block)
}


fun Assert<Configuration>.extendsFrom(other: String) {
    if (actual.extendsFrom.none { it.name == other }) {
        return expected("to extend from another configuration ${show(other)}")
    }
}


inline fun <reified D : Dependency> Assert<Configuration>.containsDependency(description: String, noinline predicate: (D) -> Boolean) {
    val dependencies = actual.dependencies.withType(D::class.java)
            .matching(predicate)
    if (dependencies.isEmpty()) {
        return expected("to contain a dependency on $description")
    }
}


fun <T> Assert<Iterable<T>>.hasSingleItem(block: (Assert<T>) -> Unit = {}) {
    val item = try {
        actual.single()
    } catch (ex: Exception) {
        return expected("to contain a single item")
    }
    assert(item, name = "[0]").all(block)
}


fun <T> Assert<Iterable<T>>.startsWith(items: Iterable<T>) {
    val itemsIter = items.iterator()
    val actualIter = actual.iterator()
    while (itemsIter.hasNext()) {
        val item = itemsIter.next()
        if (!actualIter.hasNext() || actualIter.next() != item) {
            return expected("to start with ${show(items)}\n but did not contain ${show(item)}", actual = actual)
        }
    }
}


fun <T> Assert<Iterable<T>>.containsAll(items: Iterable<T>) {
    val notContained = items.minus(actual)
    if (notContained.isNotEmpty()) {
        return expected("to contain all of ${show(items)}\n  but did not contain ${show(notContained)}", actual = actual)
    }
}
