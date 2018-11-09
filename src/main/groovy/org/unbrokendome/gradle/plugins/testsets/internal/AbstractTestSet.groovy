package org.unbrokendome.gradle.plugins.testsets.internal

import org.unbrokendome.gradle.plugins.testsets.dsl.TestSet


abstract class AbstractTestSet implements TestSet {

    @Override
    String getTestTaskName() {
        name
    }


    @Override
    String getJarTaskName() {
        "${name}Jar"
    }


    @Override
    String getSourceSetName() {
        name
    }


    @Override
    String getCompileConfigurationName() {
        "${name}Compile"
    }


    @Override
    String getCompileOnlyConfigurationName() {
        "${name}CompileOnly"
    }


    @Override
    String getCompileClasspathConfigurationName() {
        "${name}CompileClasspath"
    }


    @Override
    String getAnnotationProcessorConfigurationName() {
        "${name}AnnotationProcessor"
    }


    String getImplementationConfigurationName() {
        "${name}Implementation"
    }


    @Override
    String getRuntimeConfigurationName() {
        "${name}Runtime"
    }


    @Override
    String getRuntimeOnlyConfigurationName() {
        "${name}RuntimeOnly"
    }


    @Override
    String getRuntimeClasspathConfigurationName() {
        "${name}RuntimeClasspath"
    }


    @Override
    String getArtifactConfigurationName() {
        name
    }


    @Override
    String getClassifier() {
        name
    }
}
