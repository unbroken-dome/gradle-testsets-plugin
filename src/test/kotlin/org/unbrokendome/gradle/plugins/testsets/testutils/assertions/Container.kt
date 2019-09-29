package org.unbrokendome.gradle.plugins.testsets.testutils.assertions

import assertk.Assert
import assertk.assertions.isNull
import assertk.assertions.support.expected
import org.gradle.api.NamedDomainObjectCollection


fun <T : Any> Assert<NamedDomainObjectCollection<T>>.containsItem(name: String) =
    transform { actual ->
        actual.findByName(name) ?: expected("to contain an item named \"$name\"")
    }


fun <T : Any> Assert<NamedDomainObjectCollection<T>>.doesNotContainItem(name: String) =
    transform { actual -> actual.findByName(name) }.isNull()
