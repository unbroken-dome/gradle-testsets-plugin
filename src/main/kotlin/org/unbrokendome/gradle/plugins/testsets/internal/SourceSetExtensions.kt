package org.unbrokendome.gradle.plugins.testsets.internal

import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.reflect.TypeOf
import org.gradle.api.tasks.SourceSet


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
 * Gets additional [SourceDirectorySet] extensions that were added to this [SourceSet] by plugins.
 *
 * For example, the Groovy or Kotlin plugins each add a new `SourceDirectorySet` to a `SourceSet`.
 *
 * @return a [Sequence] containing the additional `SourceDirectorySet`s for this `SourceSet`
 */
@Suppress("ReplaceSingleLineLet")
internal fun SourceSet.getAdditionalSourceDirectorySets(): Sequence<SourceDirectorySet> {

    val sourceDirectorySetType = TypeOf.typeOf(SourceDirectorySet::class.java)

    return this.extensions.extensionsSchema
        .asSequence()
        .filter { extensionSchema ->
            sourceDirectorySetType.isAssignableFrom(extensionSchema.publicType)
        }
        .map { extensionSchema ->
            extensions.findByName(extensionSchema.name) as SourceDirectorySet
        }
}
