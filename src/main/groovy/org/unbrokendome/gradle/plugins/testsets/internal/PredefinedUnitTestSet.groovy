package org.unbrokendome.gradle.plugins.testsets.internal

import org.gradle.api.Action
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.SourceSet
import org.unbrokendome.gradle.plugins.testsets.dsl.TestSet


class PredefinedUnitTestSet extends AbstractTestSet {

    static final String NAME = "unitTest"


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
        //Make sure to be compatible with older versions of gradle that may not
        //have the compile classpath configuration
        /**if (JavaPlugin.hasProperty("TEST_COMPILE_CLASSPATH_CONFIGURATION_NAME")) {
            JavaPlugin.TEST_COMPILE_CLASSPATH_CONFIGURATION_NAME
        } else {
            JavaPlugin.TEST_COMPILE_CONFIGURATION_NAME
        }**/
        JavaPlugin.TEST_COMPILE_CLASSPATH_CONFIGURATION_NAME
    }


    @Override
    String getRuntimeConfigurationName() {
        //Make sure to be compatible with older versions of gradle that may not
        //have the runtime classpath configuration
        /**if (JavaPlugin.hasProperty("TEST_RUNTIME_CLASSPATH_CONFIGURATION_NAME")) {
            JavaPlugin.TEST_RUNTIME_CLASSPATH_CONFIGURATION_NAME
        } else {
            JavaPlugin.TEST_RUNTIME_CONFIGURATION_NAME
        }**/
        JavaPlugin.TEST_RUNTIME_CLASSPATH_CONFIGURATION_NAME
    }


    @Override
    void whenExtendsFromAdded(Action<TestSet> action) {
    }


    @Override
    void whenDirNameChanged(Action<String> action) {
    }
}
