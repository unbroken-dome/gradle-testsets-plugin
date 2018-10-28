package org.unbrokendome.gradle.plugins.testsets.internal

import org.gradle.api.Project
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.internal.HasConvention
import org.gradle.api.tasks.SourceSet
import org.unbrokendome.gradle.plugins.testsets.dsl.TestSetBase
import org.unbrokendome.gradle.plugins.testsets.dsl.TestSetObserver


internal class SourceSetObserver(
        private val project: Project)
    : TestSetObserver {

    override fun dirNameChanged(testSet: TestSetBase, oldDirName: String, newDirName: String) {

        with(testSet.sourceSet) {

            java.modifyDirName(oldDirName, newDirName)
            resources.modifyDirName(oldDirName, newDirName)

            getAdditionalSourceDirsFromConventions()
                    .forEach {
                        it.modifyDirName(oldDirName, newDirName)
                    }
        }
    }


    @Suppress("ReplaceSingleLineLet")
    private fun SourceSet.getAdditionalSourceDirsFromConventions(): Sequence<SourceDirectorySet> =
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


    private fun SourceDirectorySet.modifyDirName(oldDirName: String, newDirName: String) {

        setSrcDirs(srcDirs.map { srcDir ->
            if (srcDir.parentFile == project.file("src/$oldDirName")) {
                project.file("src/$newDirName/${srcDir.name}")
            } else {
                srcDir
            }
        })
    }
}
