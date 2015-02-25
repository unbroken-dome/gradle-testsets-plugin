package org.unbrokendome.gradle.plugins.testsets.internal;

import org.gradle.api.Project;
import org.gradle.api.tasks.SourceSet;
import org.unbrokendome.gradle.plugins.testsets.dsl.TestSet;

public abstract class AbstractConventionPropertySource {

	private final Project project;
	private final TestSet testSet;


	public AbstractConventionPropertySource(Project project, TestSet testSet) {
		this.project = project;
		this.testSet = testSet;
	}


	public Project getProject() {
		return project;
	}


	public TestSet getTestSet() {
		return testSet;
	}


	public SourceSet getSourceSet() {
		return SourceSetHelper.getSourceSet(project, testSet.getSourceSetName());
	}
}
