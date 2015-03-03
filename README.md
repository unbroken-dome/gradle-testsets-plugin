# Gradle TestSets plugin

A plugin for the Gradle build system that allows specifying test sets (like integration or acceptance tests). A test set is a logical grouping of a source set and related dependency configurations, tasks and artifacts.

## Usage

To use the TestSets plugin, include the following lines in your build script:

	plugins {
		id 'org.unbroken-dome.test-sets' version '1.0.1'
	}

The TestSets plugin will only work in conjunction with the `java` and/or `groovy` plugin.

To define a new test set, use the `testSets` DSL in the project:

    testSets {
        integrationTest
    }
    
Where "integrationTest" would be the name of the test set.

This will automatically create the following objects in your project:
* A a [source set](http://gradle.org/docs/current/userguide/java_plugin.html#N11F7B) named "integrationTest";
* A [dependency configuration](http://gradle.org/docs/current/userguide/dependency_management.html#sub:configurations) named "integrationTestCompile", which extends from "testCompile";
* A  dependency configuration named "integrationTestRuntime", which extends from "testRuntime";
* A [Test](http://gradle.org/docs/current/userguide/java_plugin.html#sec:java_test) task named "integrationTest" which will run the tests in the set;
* A [Jar](http://gradle.org/docs/current/userguide/java_plugin.html#N12A7C) task named "integrationTestJar" which will package the tests.

## Test sets DSL

A test set is a logical grouping of the following:
- a [source set](http://gradle.org/docs/current/userguide/java_plugin.html#N11F7B);
- a compile and runtime [dependency configuration](http://gradle.org/docs/current/userguide/dependency_management.html#sub:configurations);
- a [Test](http://gradle.org/docs/current/userguide/java_plugin.html#sec:java_test) task to run the tests;
- a [Jar](http://gradle.org/docs/current/userguide/java_plugin.html#N12A7C) task to package the tests;
- optionally, an [artifact](http://gradle.org/docs/current/userguide/artifact_management.html) that can be published.

### Extending other test sets

A test set can extend other test sets. This makes the test set's `compile` and `runtime` configurations extend the other test set's `compile` and `runtime` configurations, respectively.

    testSets {
        fooTest
        barTest { extendsFrom fooTest }
    }

### Predefined unit test set

The `java` and `groovy` plugins automatically define a source set named "test" to hold unit tests, as well as "testCompile" and "testRuntime" configurations to declare its dependencies, and a "test" task to run the tests. The TestSets plugin logically groups these into a predefined test set called "unitTest".

All new test sets implicitly extend the "unitTest" set, meaning that every test set's `compile` configuration will automatically extend `testCompile`, and every test set's `runtime` configuration will extend `testRuntime`.

### Changing the directory name

For a source set named "myTest", the `java` plugin by default assumes the directories `src/myTest/java` and `src/myTest/resources`. A different directory name can be specified using the `dirName` on the test set, for example:

    testSets {
        myTest { dirName = 'my-test' }
    }
    
Which would change the source set's java and resources directories to `src/my-test/java` and `src/my-test/resources`, respectively. This also works with the `groovy` source directory, if the `groovy` plugin is applied to the project.

### Publishing an artifact

Optionally, an artifact containing the test set's classes and resources can be added to the project's output. To activate this, simply set the `createArtifact` property of the test set to `true`:

    testSets {
        integrationTest { createArtifact = true }
    }
    
This will add the artifact `<projectName>-integrationTest.jar` to the project's artifacts.

