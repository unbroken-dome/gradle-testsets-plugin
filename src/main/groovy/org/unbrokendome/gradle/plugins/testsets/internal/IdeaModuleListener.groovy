package org.unbrokendome.gradle.plugins.testsets.internal;

import com.google.common.eventbus.Subscribe;
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.tasks.SourceSet;
import org.gradle.plugins.ide.idea.model.IdeaModel
import org.unbrokendome.gradle.plugins.testsets.dsl.TestSet
import org.unbrokendome.gradle.plugins.testsets.dsl.TestSetContainer


public class IdeaModuleListener {

    private final Project project;


    public IdeaModuleListener(Project project) {
        this.project = project;

        def testSets = project.testSets as TestSetContainer
        testSets.whenObjectAdded { testSetAdded(it) }
    }


    @Subscribe
    void testSetAdded(TestSet testSet) {

        def ideaModel = project.extensions.findByType IdeaModel
        if (ideaModel) {

            def ideaModule = ideaModel.module

            def sourceSet = project.sourceSets[testSet.sourceSetName] as SourceSet
            ideaModule.testSourceDirs += sourceSet.allSource.srcDirs

            ideaModule.scopes.TEST.plus += [ project.configurations[testSet.runtimeConfigurationName] ]
        }
    }
}
