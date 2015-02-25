package org.unbrokendome.gradle.plugins.testsets.internal;

import java.util.Collections;
import java.util.Set;

import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.tasks.SourceSet;
import org.unbrokendome.gradle.plugins.testsets.dsl.TestSet;

public class PredefinedUnitTestSet extends AbstractTestSet {

	public static final String NAME = "unitTest";


	@Override
	public String getName() {
		return NAME;
	}
	

	@Override
	public boolean isCreateArtifact() {
		return false;
	}

	
	@Override
	public String getDirName() {
		return SourceSet.TEST_SOURCE_SET_NAME;
	}
	

	@Override
	public Set<TestSet> getExtendsFrom() {
		return Collections.emptySet();
	}


	@Override
	public String getTestTaskName() {
		return JavaPlugin.TEST_TASK_NAME;
	}


	@Override
	public String getSourceSetName() {
		return SourceSet.TEST_SOURCE_SET_NAME;
	}


	@Override
	public String getCompileConfigurationName() {
		return JavaPlugin.TEST_COMPILE_CONFIGURATION_NAME;
	}


	@Override
	public String getRuntimeConfigurationName() {
		return JavaPlugin.TEST_RUNTIME_CONFIGURATION_NAME;
	}
}
