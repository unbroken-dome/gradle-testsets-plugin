package org.unbrokendome.gradle.plugins.testsets.internal

import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.SourceSet
import org.unbrokendome.gradle.plugins.testsets.dsl.TestSet

import java.util.function.BiConsumer
import java.util.function.Consumer

class PredefinedUnitTestSet extends AbstractTestSet {

	static final String NAME = "unitTest"


	@Override
	String getName() {
		NAME
	}
	

	@Override
	boolean isCreateArtifact() {
		false
	}

	
	@Override
	String getDirName() {
		SourceSet.TEST_SOURCE_SET_NAME
	}
	

	@Override
	Set<TestSet> getExtendsFrom() {
		Collections.emptySet()
	}


	@Override
	String getTestTaskName() {
		JavaPlugin.TEST_TASK_NAME
	}


	@Override
	String getSourceSetName() {
		SourceSet.TEST_SOURCE_SET_NAME
	}


	@Override
	String getCompileConfigurationName() {
		JavaPlugin.TEST_COMPILE_CONFIGURATION_NAME
	}


	@Override
	String getRuntimeConfigurationName() {
		JavaPlugin.TEST_RUNTIME_CONFIGURATION_NAME
	}


    @Override
    void whenExtendsFromAdded(Consumer<TestSet> action) {
    }


    @Override
    void whenDirNameChanged(Consumer<String> action) {
    }
}
