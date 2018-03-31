package org.unbrokendome.gradle.plugins.testsets.internal

import groovy.transform.CompileStatic
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.SourceSet
import org.gradle.util.VersionNumber
import org.unbrokendome.gradle.plugins.testsets.dsl.TestSet


class PredefinedUnitTestSet extends AbstractTestSet {

    static final String NAME = "unitTest"

    private final VersionNumber gradleVersion


    PredefinedUnitTestSet(Project project) {
        gradleVersion = VersionNumber.parse(project.gradle.gradleVersion)
    }


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
    String getCompileOnlyConfigurationName() {
        if (gradleVersion >= VersionNumber.parse('2.12')) {
            return JavaPlugin.TEST_COMPILE_ONLY_CONFIGURATION_NAME;
        } else {
            return null
        }
    }


    @Override
    String getImplementationConfigurationName() {
        if (gradleVersion >= VersionNumber.parse('3.4')) {
            return JavaPlugin.TEST_IMPLEMENTATION_CONFIGURATION_NAME
        } else {
            return null
        }
    }


    @Override
    String getRuntimeConfigurationName() {
        //noinspection GrDeprecatedAPIUsage
        JavaPlugin.TEST_RUNTIME_CONFIGURATION_NAME
    }


    @Override
    String getRuntimeOnlyConfigurationName() {
        if (gradleVersion >= VersionNumber.parse('3.4')) {
            return JavaPlugin.TEST_RUNTIME_ONLY_CONFIGURATION_NAME
        } else {
            return null
        }
    }


    @Override
    void whenExtendsFromAdded(Action<TestSet> action) {
    }


    @Override
    void whenDirNameChanged(Action<String> action) {
    }
}
