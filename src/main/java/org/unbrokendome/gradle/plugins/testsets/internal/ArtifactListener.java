package org.unbrokendome.gradle.plugins.testsets.internal;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.internal.artifacts.publish.ArchivePublishArtifact;
import org.gradle.api.tasks.bundling.Jar;
import org.unbrokendome.gradle.plugins.testsets.dsl.TestSet;
import org.unbrokendome.gradle.plugins.testsets.internal.events.TestSetAddedEvent;

import com.google.common.eventbus.Subscribe;

public class ArtifactListener {

	private final Project project;


	public ArtifactListener(Project project) {
		this.project = project;
	}


	@Subscribe
	public void testSetAdded(TestSetAddedEvent event) {
		final TestSet testSet = event.getTestSet();

		project.afterEvaluate(new Action<Project>() {

			@Override
			public void execute(Project project) {
				if (testSet.isCreateArtifact()) {
					Configuration artifactConfiguration = project.getConfigurations().maybeCreate(
							testSet.getArtifactConfigurationName());

					Jar jarTask = (Jar) project.getTasks().findByName(testSet.getJarTaskName());
					ArchivePublishArtifact artifact = new ArchivePublishArtifact(jarTask);
					artifact.setClassifier(testSet.getClassifier());
					artifactConfiguration.getArtifacts().add(artifact);
				}
			}
		});
	}
}
