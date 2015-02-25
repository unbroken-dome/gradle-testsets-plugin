package org.unbrokendome.gradle.plugins.testsets.dsl;

import java.util.Set;

import org.gradle.api.Named;

public interface TestSet extends Named {

	Set<TestSet> getExtendsFrom();


	boolean isCreateArtifact();


	String getClassifier();


	String getDirName();


	String getTestTaskName();


	String getJarTaskName();


	String getSourceSetName();


	String getCompileConfigurationName();


	String getRuntimeConfigurationName();


	String getArtifactConfigurationName();
}
