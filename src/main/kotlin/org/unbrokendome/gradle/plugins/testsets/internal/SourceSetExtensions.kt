package org.unbrokendome.gradle.plugins.testsets.internal

import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.internal.HasConvention
import org.gradle.api.tasks.SourceSet
import org.unbrokendome.gradle.plugins.testsets.util.capitalize


/**
 * Gets all [SourceDirectorySet]s for this [SourceSet]. This includes the [allJava][SourceSet.getAllJava]
 * and [resources][SourceSet.getResources] directory sets, as well as any `SourceDirectorySet`s added by plugin
 * conventions (e.g. `groovy`, `kotlin`).
 *
 * @return a [Sequence] containing all [SourceDirectorySet]s
 */
internal fun SourceSet.getAllSourceDirectorySets(): Sequence<SourceDirectorySet> =
        getAllCodeSourceDirectorySets() + sequenceOf(resources)


/**
 * Gets all [SourceDirectorySet]s for this [SourceSet] that contain code. This includes the
 * [java][SourceSet.getJava] directory set as well as any `SourceDirectorySet`s added by plugin conventions
 * (e.g. `groovy`, `kotlin`) but not the `resources`.
 *
 * @return a [Sequence] containing all code [SourceDirectorySet]s
 */
internal fun SourceSet.getAllCodeSourceDirectorySets(): Sequence<SourceDirectorySet> =
        sequenceOf(java) + getAdditionalSourceDirectorySets()


/**
 * Gets additional [SourceDirectorySet]s that were added to this [SourceSet] by plugin conventions.
 *
 * For example, the Groovy or Kotlin plugins each add a new `SourceDirectorySet` to a `SourceSet`.
 *
 * @return a [Sequence] containing the additional `SourceDirectorySet`s for this `SourceSet`
 */
@Suppress("ReplaceSingleLineLet")
internal fun SourceSet.getAdditionalSourceDirectorySets(): Sequence<SourceDirectorySet> =
        (this as? HasConvention)?.let { sourceSetConventions ->
            sourceSetConventions.convention.plugins.asSequence()
                    .map { (conventionName, convention) ->
                        convention.javaClass.methods
                                .find {
                                    it.name == "get${conventionName.capitalize()}" &&
                                            SourceDirectorySet::class.java.isAssignableFrom(it.returnType) &&
                                            it.parameterCount == 0
                                }
                                ?.let { method ->
                                    method.invoke(convention) as SourceDirectorySet
                                }
                    }
                    .filterNotNull()
        } ?: emptySequence()
