package org.unbrokendome.gradle.plugins.testsets.internal;

import java.io.File;

import org.gradle.api.Project;
import org.gradle.api.file.FileCollection;
import org.gradle.api.plugins.JavaBasePlugin;
import org.gradle.api.tasks.testing.Test;
import org.unbrokendome.gradle.plugins.testsets.dsl.TestSet;
import org.unbrokendome.gradle.plugins.testsets.internal.conventions.ConventionHelper;
import org.unbrokendome.gradle.plugins.testsets.internal.conventions.ConventionProperty;
import org.unbrokendome.gradle.plugins.testsets.internal.events.TestSetAddedEvent;

import com.google.common.eventbus.Subscribe;

public class TestTaskListener {

	private final Project project;


	public TestTaskListener(Project project) {
		this.project = project;
	}


	@Subscribe
	public void testSetAdded(TestSetAddedEvent event) {
		TestSet testSet = event.getTestSet();

		Test testTask = project.getTasks().create(testSet.getTestTaskName(), Test.class);

		ConventionHelper.applyConventionProperties(testTask, new TestTaskConventionPropertySource(project, testSet));
	}


	private static class TestTaskConventionPropertySource extends AbstractConventionPropertySource {

		public TestTaskConventionPropertySource(Project project, TestSet testSet) {
			super(project, testSet);
		}


		@ConventionProperty("description")
		public String getDescription() {
			return "Runs the " + getTestSet().getName() + " tests";
		}


		@ConventionProperty("group")
		public String getGroup() {
			return JavaBasePlugin.VERIFICATION_GROUP;
		}


		@ConventionProperty("testClassesDir")
		public File getTestClassesDir() {
			return getSourceSet().getOutput().getClassesDir();
		}


		@ConventionProperty("classpath")
		public FileCollection getClasspath() {
			return getSourceSet().getRuntimeClasspath();
		}
	}
}
