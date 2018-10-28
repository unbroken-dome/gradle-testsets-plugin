package org.unbrokendome.gradle.plugins.testsets.util

import groovy.lang.Closure
import org.gradle.api.Action
import org.gradle.util.ConfigureUtil


fun <T : Any> Closure<*>.toAction(): Action<T> =
        ConfigureUtil.configureUsing(this)
