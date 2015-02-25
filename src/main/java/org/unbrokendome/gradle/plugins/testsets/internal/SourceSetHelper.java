package org.unbrokendome.gradle.plugins.testsets.internal;

import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;

public final class SourceSetHelper {

	public static SourceSetContainer getSourceSets(Project project) {
		JavaPluginConvention javaConvention = project.getConvention().getPlugin(JavaPluginConvention.class);
		return javaConvention.getSourceSets();
	}


	public static SourceSet getSourceSet(Project project, String sourceSetName) {
		return getSourceSets(project).findByName(sourceSetName);
	}
}
