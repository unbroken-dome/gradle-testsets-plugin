package org.unbrokendome.gradle.plugins.testsets.internal;

import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.unbrokendome.gradle.plugins.testsets.dsl.TestSet;
import org.unbrokendome.gradle.plugins.testsets.internal.events.ExtendsFromAddedEvent;

import com.google.common.eventbus.Subscribe;

public class ConfigurationDependencyListener {

	private final Project project;


	public ConfigurationDependencyListener(Project project) {
		this.project = project;
	}


	@Subscribe
	public void extendsFromAdded(ExtendsFromAddedEvent event) {
		TestSet testSet = event.getTestSet();
		TestSet superTestSet = event.getSuperTestSet();
		
		addConfigurationExtension(testSet.getCompileConfigurationName(), superTestSet.getCompileConfigurationName());
		addConfigurationExtension(testSet.getRuntimeConfigurationName(), superTestSet.getRuntimeConfigurationName());
	}


	private void addConfigurationExtension(String configurationName, String superConfigurationName) {
		ConfigurationContainer configurations = project.getConfigurations();
		Configuration testSetCompileConfiguration = configurations.findByName(configurationName);
		Configuration mainCompileConfiguration = configurations.findByName(superConfigurationName);

		testSetCompileConfiguration.extendsFrom(mainCompileConfiguration);
	}
}
