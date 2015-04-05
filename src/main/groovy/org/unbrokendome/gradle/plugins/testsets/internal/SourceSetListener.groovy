package org.unbrokendome.gradle.plugins.testsets.internal

import org.gradle.api.Project
import org.gradle.api.internal.plugins.DslObject
import org.gradle.api.tasks.GroovySourceSet
import org.gradle.api.tasks.SourceSet
import org.unbrokendome.gradle.plugins.testsets.dsl.TestSet
import org.unbrokendome.gradle.plugins.testsets.dsl.TestSetContainer

class SourceSetListener {
	
	private final Project project
	
	
	SourceSetListener(Project project) {
		this.project = project

        def testSets = project.testSets as TestSetContainer
        testSets.whenObjectAdded { testSetAdded(it) }
	}
	

	void testSetAdded(TestSet testSet) {
		createSourceSet(testSet)
		createDependency(testSet)

        testSet.whenDirNameChanged { dirNameChanged(testSet) }
	}


	private void createSourceSet(TestSet testSet) {
		// Remember: creating a SourceSet will also implicitly create a compile and runtime configuration
		project.sourceSets.create testSet.sourceSetName
	}


	private void createDependency(TestSet testSet) {
        project.dependencies.add testSet.compileConfigurationName, getMainSourceSet().output
	}


	private SourceSet getMainSourceSet() {
        project.sourceSets[SourceSet.MAIN_SOURCE_SET_NAME]
	}
	
	
	void dirNameChanged(TestSet testSet) {
		String dirName = testSet.dirName
		
		def sourceSet = project.sourceSets[testSet.sourceSetName] as SourceSet
		
		applyJavaSrcDir(sourceSet, dirName)
		applyResourcesSrcDir(sourceSet, dirName)
		applyGroovySrcDir(sourceSet, dirName)
	}


	private void applyResourcesSrcDir(SourceSet sourceSet, String dirName) {
        sourceSet.resources.srcDirs = [ "src/$dirName/resources" ]
	}


	private void applyJavaSrcDir(SourceSet sourceSet, String dirName) {
        sourceSet.java.srcDirs = [ "src/$dirName/java" ]
	}
	
	
	private void applyGroovySrcDir(SourceSet sourceSet, String dirName) {
		def groovySourceSet = new DslObject(sourceSet).convention.findPlugin GroovySourceSet
		if (groovySourceSet != null) {
            groovySourceSet.groovy.srcDirs = [ "src/$dirName/groovy" ]
		}
	}

}
