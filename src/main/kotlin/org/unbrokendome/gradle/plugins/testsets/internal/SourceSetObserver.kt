package org.unbrokendome.gradle.plugins.testsets.internal

import org.gradle.api.Project
import org.gradle.api.file.SourceDirectorySet
import org.unbrokendome.gradle.plugins.testsets.dsl.TestSetBase
import org.unbrokendome.gradle.plugins.testsets.dsl.TestSetObserver


internal class SourceSetObserver(
        private val project: Project)
    : TestSetObserver {

    override fun dirNameChanged(testSet: TestSetBase, oldDirName: String, newDirName: String) {
        testSet.sourceSet.getAllSourceDirectorySets()
                .forEach {
                    it.modifyDirName(oldDirName, newDirName)
                }
    }


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
