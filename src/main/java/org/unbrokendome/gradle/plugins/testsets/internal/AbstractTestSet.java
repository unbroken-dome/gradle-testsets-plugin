package org.unbrokendome.gradle.plugins.testsets.internal;

import org.unbrokendome.gradle.plugins.testsets.dsl.TestSet;

public abstract class AbstractTestSet implements TestSet {

	@Override
	public String getTestTaskName() {
		return getName();
	}


	@Override
	public String getJarTaskName() {
		return getName() + "Jar";
	}


	@Override
	public String getSourceSetName() {
		return getName();
	}


	@Override
	public String getCompileConfigurationName() {
		return getName() + "Compile";
	}


	@Override
	public String getRuntimeConfigurationName() {
		return getName() + "Runtime";
	}


	@Override
	public String getArtifactConfigurationName() {
		return getName();
	}


	@Override
	public String getClassifier() {
		return getName();
	}
}
