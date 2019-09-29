package org.unbrokendome.gradle.plugins.testsets.testutils.assertions

import assertk.Assert
import assertk.assertions.support.expected
import assertk.assertions.support.show
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.Dependency


fun Assert<Configuration>.extendsFrom(other: String) = given { actual ->
    if (actual.extendsFrom.none { it.name == other }) {
        expected("to extend from another configuration ${show(other)}", actual = actual)
    }
}


inline fun <reified D : Dependency> Assert<Configuration>.containsDependency(
    description: String,
    noinline predicate: (D) -> Boolean
) = given { actual ->
    val dependencies = actual.dependencies.withType(D::class.java)
        .matching(predicate)
    if (dependencies.isEmpty()) {
        expected("to contain a dependency on $description", actual = actual)
    }
}
