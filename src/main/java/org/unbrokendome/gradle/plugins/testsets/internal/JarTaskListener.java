package org.unbrokendome.gradle.plugins.testsets.internal;

import java.util.concurrent.Callable;

import org.gradle.api.Project;
import org.gradle.api.plugins.BasePlugin;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.bundling.Jar;
import org.unbrokendome.gradle.plugins.testsets.dsl.TestSet;
import org.unbrokendome.gradle.plugins.testsets.internal.conventions.ConventionHelper;
import org.unbrokendome.gradle.plugins.testsets.internal.conventions.ConventionProperty;
import org.unbrokendome.gradle.plugins.testsets.internal.events.TestSetAddedEvent;

import com.google.common.eventbus.Subscribe;

public class JarTaskListener {

	private final Project project;


	public JarTaskListener(Project project) {
		this.project = project;
	}


	@Subscribe
	public void testSetAdded(TestSetAddedEvent event) {
		final TestSet testSet = event.getTestSet();

		Jar jarTask = project.getTasks().create(testSet.getJarTaskName(), Jar.class);

		ConventionHelper.applyConventionProperties(jarTask, new JarTaskConventionPropertySource(project, testSet));

		jarTask.from(new Callable<Object>() {

			@Override
			public Object call() throws Exception {
				SourceSet sourceSet = SourceSetHelper.getSourceSet(project, testSet.getSourceSetName());
				return sourceSet.getOutput();
			}
		});
	}


	private static class JarTaskConventionPropertySource extends AbstractConventionPropertySource {

		public JarTaskConventionPropertySource(Project project, TestSet testSet) {
			super(project, testSet);
		}


		@ConventionProperty("description")
		public String getDescription() {
			return "Assembles a jar archive containing the " + getTestSet().getName() + " classes.";
		}


		@ConventionProperty("group")
		public String getGroup() {
			return BasePlugin.BUILD_GROUP;
		}


		@ConventionProperty("classifier")
		public String getClassifier() {
			return getTestSet().getClassifier();
		}
	}
}
