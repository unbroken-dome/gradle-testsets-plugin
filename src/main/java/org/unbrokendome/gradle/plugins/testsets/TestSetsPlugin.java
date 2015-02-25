package org.unbrokendome.gradle.plugins.testsets;

import javax.inject.Inject;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.internal.reflect.Instantiator;
import org.unbrokendome.gradle.plugins.testsets.internal.ConfigurationDependencyListener;
import org.unbrokendome.gradle.plugins.testsets.internal.ArtifactListener;
import org.unbrokendome.gradle.plugins.testsets.internal.DefaultTestSetContainer;
import org.unbrokendome.gradle.plugins.testsets.internal.EclipseClasspathListener;
import org.unbrokendome.gradle.plugins.testsets.internal.JarTaskListener;
import org.unbrokendome.gradle.plugins.testsets.internal.SourceSetListener;
import org.unbrokendome.gradle.plugins.testsets.internal.TestTaskListener;

import com.google.common.eventbus.EventBus;

public class TestSetsPlugin implements Plugin<Project> {

	private final Instantiator instantiator;
	private final EventBus eventBus = new EventBus("testSetEvents");


	@Inject
	public TestSetsPlugin(Instantiator instantiator) {
		this.instantiator = instantiator;
	}


	@Override
	public void apply(Project project) {
		project.getPlugins().apply(JavaPlugin.class);
		
		eventBus.register(new SourceSetListener(project));
		eventBus.register(new ConfigurationDependencyListener(project));
		eventBus.register(new TestTaskListener(project));
		eventBus.register(new JarTaskListener(project));
		eventBus.register(new ArtifactListener(project));
		eventBus.register(new EclipseClasspathListener(project));
		
		project.getExtensions().create("testSets", DefaultTestSetContainer.class,
				instantiator, eventBus);
	}
}
