# Gradle TestSets plugin

A plugin for the Gradle build system that allows specifying test sets (like integration or acceptance tests). A test set is a logical grouping of a source set and related dependency configurations, tasks and artifacts.

## Usage

### Applying the plugin

To use the TestSets plugin, include either of the following in your build script:

#### New Plugins DSL (Gradle 2.1+)

```groovy
plugins {
    id 'org.unbroken-dome.test-sets' version '1.1.0'
}
```

#### Traditional (Gradle 1.x/2.0)

```groovy
buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath 'org.unbroken-dome.gradle-plugins:gradle-testsets-plugin:1.1.0'
    }
}

apply plugin: 'org.unbroken-dome.test-sets'
```

### Prerequisites

The TestSets plugin will only work in conjunction with the `java` and/or `groovy` plugin.

You will need to run Gradle with a JDK 1.7 or higher to use the plugin.

## Test sets DSL

The test set is a logical grouping of the following:

- a [source set](http://gradle.org/docs/current/userguide/java_plugin.html#N11F7B);
- a compile and runtime [dependency configuration](http://gradle.org/docs/current/userguide/dependency_management.html#sub:configurations);
- a [Test](http://gradle.org/docs/current/userguide/java_plugin.html#sec:java_test) task to run the tests;
- a [Jar](http://gradle.org/docs/current/userguide/java_plugin.html#N12A7C) task to package the tests;
- optionally, an [artifact](http://gradle.org/docs/current/userguide/artifact_management.html) that can be published.

To define a new test set, use the `testSets` DSL in the project:

```groovy
testSets {
    integrationTest
}
```

Where "integrationTest" would be the name of the test set.

This would automatically create the following objects:

* A source set named "integrationTest";
* A dependency configuration named "integrationTestCompile", which extends from "testCompile";
* A dependency configuration named "integrationTestRuntime", which extends from "testRuntime";
* A Test task named "integrationTest" which will run the tests in the set;
* A Jar task named "integrationTestJar" which will package the tests.


### Extending other test sets

A test set can extend other test sets. This makes the test set's `compile` and `runtime` configurations extend the other test set's `compile` and `runtime` configurations, respectively.

```groovy
testSets {
    fooTest
    barTest { extendsFrom fooTest }
}
```

It does _not_ mean, however, that the source (classes / resources) of the extended test set will be available to the
extending test set. For this you would still have to create a dependency:

```groovy
dependencies {
    fooTestCompile sourceSets.barTest.output
}
```

### Predefined unit test set

The `java` and `groovy` plugins automatically define a source set named "test" to hold unit tests, as well as "testCompile" and "testRuntime" configurations to declare its dependencies, and a "test" task to run the tests. The TestSets plugin logically groups these into a predefined test set called "unitTest".

All new test sets implicitly extend the "unitTest" set, meaning that every test set's `compile` configuration will automatically extend `testCompile`, and every test set's `runtime` configuration will extend `testRuntime`.

### Changing the directory name

For a source set named "myTest", the `java` plugin by default assumes the directories `src/myTest/java` and `src/myTest/resources`. A different directory name can be specified using the `dirName` on the test set, for example:

```groovy
testSets {
    myTest { dirName = 'my-test' }
}
```
    
Which would change the source set's java and resources directories to `src/my-test/java` and `src/my-test/resources`, respectively. This also works with the `groovy` source directory, if the `groovy` plugin is applied to the project.

### Publishing an artifact

Optionally, an artifact containing the test set's classes and resources can be added to the project's output. To activate this, simply set the `createArtifact` property of the test set to `true`:

```groovy
testSets {
    integrationTest { createArtifact = true }
}
```
    
This will add the artifact `<projectName>-integrationTest.jar` to the project's artifacts.


## IDE Support

The plugin supports Eclipse and IntelliJ IDEA through the `eclipse` and `idea` plugins. If these plugins are active in
a project, each test set's source sets and dependencies will be added to the Eclipse/IDEA project.

Neither Eclipse nor IntelliJ IDEA support the notion of multiple test sets per project / module, so what the plugin does
is only a "best fit" so you can at least run the tests from your IDE. These tests will never be executed in isolation,
however, which may become an issue if you have files of the same name (e.g. log4j2-test.xml) in different source sets.

### Eclipse

If your project applies the `eclipse` plugin, the TestSets plugin will automatically add each test set's dependencies
to the classpath. SourceSets that are generated for a test set are automatically mapped to source folders in Eclipse,
without any further configuration. (Eclipse does not distinguish between production and test source folders.)

Eclipse does not support different scopes for dependencies; all dependencies (main, test and additional test sets) are
thrown into a shared "Gradle classpath container".

### IntelliJ IDEA

If your project applies the `idea` plugin, the TestSets plugin will add the source set root directories as source folders
to your IDEA module and mark them as "test sources root" (these folders will be marked with a green folder icon in the
project view).

Dependencies for each test set are added under TEST scope (which is the same scope that is used for unit tests).


