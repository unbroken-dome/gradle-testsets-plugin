package org.unbrokendome.gradle.plugins.testsets.dsl

import org.gradle.api.Named
import java.util.function.Consumer

interface TestSet extends Named {

    Set<TestSet> getExtendsFrom()


    boolean isCreateArtifact()


    String getClassifier()


    String getDirName()


    String getTestTaskName()


    String getJarTaskName()


    String getSourceSetName()


    String getCompileConfigurationName()


    String getRuntimeConfigurationName()


    String getArtifactConfigurationName()


    void whenExtendsFromAdded(Consumer<TestSet> action)
    void whenDirNameChanged(Consumer<String> action)
}
