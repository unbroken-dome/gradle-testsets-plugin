package org.unbrokendome.gradle.plugins.testsets.util

import org.gradle.api.NamedDomainObjectCollection


operator fun <T : Any> NamedDomainObjectCollection<T>.get(name: String): T =
        getByName(name)
