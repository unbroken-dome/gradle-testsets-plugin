package org.unbrokendome.gradle.plugins.testsets.testutils.assertions

import assertk.Assert
import assertk.assertions.support.expected
import assertk.assertions.support.show


fun <T> Assert<Iterable<T>>.hasSingleItem() =
    transform(name = "$name[0]") { actual ->

        try {
            actual.single()
        } catch (ex: Exception) {
            expected("to contain a single item", actual = actual)
        }

    }


fun <T> Assert<Iterable<T>>.startsWith(items: Iterable<T>) =
    given { actual ->
        val itemsIter = items.iterator()
        val actualIter = actual.iterator()
        while (itemsIter.hasNext()) {
            val item = itemsIter.next()
            if (!actualIter.hasNext() || actualIter.next() != item) {
                expected("to start with ${show(items)}\n but did not contain ${show(item)}", actual = actual)
            }
        }
    }
