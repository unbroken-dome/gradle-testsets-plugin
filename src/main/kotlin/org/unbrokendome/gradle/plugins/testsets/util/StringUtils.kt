package org.unbrokendome.gradle.plugins.testsets.util

import java.util.*


internal fun String.capitalize() =
    replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
