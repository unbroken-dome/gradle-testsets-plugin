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
import java.lang.IllegalStateException


inline fun <reified E : Any> Assert<*>.hasExtension(name: String? = null, noinline block: (Assert<E>) -> Unit = {}) {
    this.given { actual ->
        if (actual !is ExtensionAware) {
            expected("to be ExtensionAware")
        }
        val extensions = actual.extensions

        val extension: E = if (name != null) {
            extensions.findByName(name)
                .let {
                    if (it == null) {
                        expected("to have an extension named \"$name\" of type ${show(E::class)}")
                    }
                    if (it !is E) {
                        expected("to have an extension named \"$name\" of type ${show(E::class)}, but actual type was: ${show(it.javaClass)}")
                    }
                    it
                } as E
        } else {
            extensions.findByType(E::class.java)
                .let {
                    if (it == null) {
                        expected("to have an extension of type ${show(E::class)}")
                    }
                    it
                } as E
        }

        assertThat(extension, name = "extension " + (name?.let { "\"$it\"" } ?: show(E::class))).all(block)
    }
}


inline fun <reified E : Any> Assert<*>.hasConvention(noinline block: (Assert<E>) -> Unit = {}) {
    this.given { actual ->
        if (actual !is HasConvention) {
            expected("to have conventions")
        }
        val convention = actual.convention

        val conventionObject: E =
            try {
                convention.getPlugin(E::class.java)
            } catch (ex: IllegalStateException) {
                expected("to have a convention of type ${show(E::class)}")
            }

        assertThat(conventionObject, name = "convention object ${show(E::class)}").all(block)
    }
}



fun <T : Any> Assert<NamedDomainObjectCollection<T>>.containsItem(name: String, block: (Assert<T>) -> Unit = {}) {
    this.given { actual ->
        val item = actual.findByName(name)
            ?: expected("to contain an item named \"$name\"", actual = actual.toList())
        assertThat(item, name = name).all(block)
    }
}


fun Assert<Configuration>.extendsFrom(other: String) {
    this.given { actual ->
        if (actual.extendsFrom.none { it.name == other }) {
            expected("to extend from another configuration ${show(other)}")
        }
    }
}


inline fun <reified D : Dependency> Assert<Configuration>.containsDependency(description: String, noinline predicate: (D) -> Boolean) {
    this.given { actual ->
        val dependencies = actual.dependencies.withType(D::class.java)
            .matching(predicate)
        if (dependencies.isEmpty()) {
            expected("to contain a dependency on $description")
        }
    }
}


fun <T> Assert<Iterable<T>>.hasSingleItem(block: (Assert<T>) -> Unit = {}) {
    this.given { actual ->
        val item = try {
            actual.single()
        } catch (ex: Exception) {
            expected("to contain a single item")
        }
        assertThat(item, name = "[0]").all(block)
    }
}


fun <T> Assert<Iterable<T>>.startsWith(items: Iterable<T>) {
    this.given { actual ->
        val itemsIter = items.iterator()
        val actualIter = actual.iterator()
        while (itemsIter.hasNext()) {
            val item = itemsIter.next()
            if (!actualIter.hasNext() || actualIter.next() != item) {
                expected("to start with ${show(items)}\n but did not contain ${show(item)}", actual = actual)
            }
        }
    }
}


fun <T> Assert<Iterable<T>>.containsAll(items: Iterable<T>) {
    this.given { actual ->
        val notContained = items.minus(actual)
        if (notContained.isNotEmpty()) {
            expected("to contain all of ${show(items)}\n  but did not contain ${show(notContained)}", actual = actual)
        }
    }
}
