package org.unbrokendome.gradle.plugins.testsets.testutils.assertions

import assertk.Assert
import assertk.assertions.support.expected
import assertk.assertions.support.show
import org.gradle.api.plugins.ExtensionAware


inline fun <reified E : Any> Assert<*>.hasExtension(name: String? = null) =
    transform("extension " + (name?.let { "\"$it\"" } ?: show(E::class))) { actual ->
        if (actual !is ExtensionAware) {
            expected("to be ExtensionAware")
        }
        val extensions = actual.extensions

        if (name != null) {
            extensions.findByName(name)
                .let {
                    if (it == null) {
                        expected("to have an extension named \"$name\" of type ${show(E::class)}")
                    }
                    if (it !is E) {
                        expected(
                            "to have an extension named \"$name\" of type ${show(E::class)}, but actual type was: ${show(
                                it.javaClass
                            )}"
                        )
                    }
                    it
                }
        } else {
            extensions.findByType(E::class.java)
                .let {
                    if (it == null) {
                        expected("to have an extension of type ${show(E::class)}")
                    }
                    it
                }
        }
    }
