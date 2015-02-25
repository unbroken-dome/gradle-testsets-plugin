package org.unbrokendome.gradle.plugins.testsets.internal;

import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.plugins.ide.eclipse.model.EclipseClasspath;
import org.gradle.plugins.ide.eclipse.model.EclipseModel;
import org.unbrokendome.gradle.plugins.testsets.dsl.TestSet;
import org.unbrokendome.gradle.plugins.testsets.internal.events.TestSetAddedEvent;

import com.google.common.eventbus.Subscribe;

public class EclipseClasspathListener {

	private final Project project;


	public EclipseClasspathListener(Project project) {
		this.project = project;
	}


	@Subscribe
	public void testSetAdded(TestSetAddedEvent event) {
		
		EclipseModel eclipseModel = project.getExtensions().findByType(EclipseModel.class);
		if (eclipseModel != null) {

			TestSet testSet = event.getTestSet();

			ConfigurationContainer configurations = project.getConfigurations();

			EclipseClasspath eclipseClasspath = eclipseModel.getClasspath();

			Configuration testSetCompileConfiguration = configurations
					.findByName(testSet.getCompileConfigurationName());
			if (testSetCompileConfiguration != null) {
				eclipseClasspath.getPlusConfigurations().add(testSetCompileConfiguration);
			}

			Configuration testSetRuntimeConfiguration = configurations
					.findByName(testSet.getRuntimeConfigurationName());
			if (testSetRuntimeConfiguration != null) {
				eclipseClasspath.getPlusConfigurations().add(testSetRuntimeConfiguration);
			}
		}
	}
}
