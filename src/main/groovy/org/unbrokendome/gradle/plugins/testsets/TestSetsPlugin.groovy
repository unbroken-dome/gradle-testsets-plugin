package org.unbrokendome.gradle.plugins.testsets

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.internal.reflect.Instantiator
import org.unbrokendome.gradle.plugins.testsets.internal.*

import javax.inject.Inject

public class TestSetsPlugin implements Plugin<Project> {

	private final Instantiator instantiator


	@Inject
	public TestSetsPlugin(Instantiator instantiator) {
		this.instantiator = instantiator
	}


	@Override
	void apply(Project project) {
		project.apply plugin: JavaPlugin

        project.extensions.create('testSets', DefaultTestSetContainer, instantiator)

        instantiator.newInstance SourceSetListener, project
        instantiator.newInstance ConfigurationDependencyListener, project
        instantiator.newInstance TestTaskListener, project
        instantiator.newInstance JarTaskListener, project
        instantiator.newInstance ArtifactListener, project
        instantiator.newInstance EclipseClasspathListener, project
		instantiator.newInstance IdeaModuleListener, project
	}
}
