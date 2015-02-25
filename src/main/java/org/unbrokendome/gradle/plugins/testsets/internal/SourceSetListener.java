package org.unbrokendome.gradle.plugins.testsets.internal;

import java.util.Collections;

import org.gradle.api.Project;
import org.gradle.api.internal.plugins.DslObject;
import org.gradle.api.tasks.GroovySourceSet;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;
import org.unbrokendome.gradle.plugins.testsets.dsl.TestSet;
import org.unbrokendome.gradle.plugins.testsets.internal.events.DirNameChangedEvent;
import org.unbrokendome.gradle.plugins.testsets.internal.events.TestSetAddedEvent;

import com.google.common.eventbus.Subscribe;


public class SourceSetListener {
	
	private final Project project;
	
	
	public SourceSetListener(Project project) {
		this.project = project;
	}
	
	
	@Subscribe
	public void testSetAdded(TestSetAddedEvent event) {
		TestSet testSet = event.getTestSet();
		createSourceSet(testSet);
		createDependency(testSet);
	}


	private void createSourceSet(TestSet testSet) {
		SourceSetContainer sourceSets = SourceSetHelper.getSourceSets(project);
		
		// Remember: creating a SourceSet will also implicitly create a compile and runtime configuration
		sourceSets.create(testSet.getSourceSetName());
	}


	private void createDependency(TestSet testSet) {
		SourceSet mainSourceSet = getMainSourceSet();
		project.getDependencies().add(testSet.getCompileConfigurationName(), mainSourceSet.getOutput());
	}


	private SourceSet getMainSourceSet() {
		SourceSetContainer sourceSets = SourceSetHelper.getSourceSets(project);
		return sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME);
	}
	
	
	@Subscribe
	public void dirNameChanged(DirNameChangedEvent event) {
		TestSet testSet = event.getTestSet();
		String dirName = event.getDirName();
		
		SourceSet sourceSet = SourceSetHelper.getSourceSet(project, testSet.getSourceSetName());
		
		applyJavaSrcDir(sourceSet, dirName);
		applyResourcesSrcDir(sourceSet, dirName);
		applyGroovySrcDir(sourceSet, dirName);
	}


	private void applyResourcesSrcDir(SourceSet sourceSet, String dirName) {
		String resourcesSrcDir = String.format("src/%s/resources", dirName);
		sourceSet.getResources().setSrcDirs(Collections.singleton(resourcesSrcDir));
	}


	private void applyJavaSrcDir(SourceSet sourceSet, String dirName) {
		String javaSrcDir = String.format("src/%s/java", dirName);
		sourceSet.getJava().setSrcDirs(Collections.singleton(javaSrcDir));
	}
	
	
	private void applyGroovySrcDir(SourceSet sourceSet, String dirName) {
		GroovySourceSet groovySourceSet = new DslObject(sourceSet).getConvention().findPlugin(GroovySourceSet.class);
		if (groovySourceSet != null) {
			String groovySrcDir = String.format("src/%s/groovy", dirName);
			groovySourceSet.getGroovy().setSrcDirs(Collections.singleton(groovySrcDir));
		}
	}

}
