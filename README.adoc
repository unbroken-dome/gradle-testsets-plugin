ifdef::env-github[]
:tip-caption: :bulb:
:note-caption: :information_source:
:important-caption: :heavy_exclamation_mark:
:caution-caption: :fire:
:warning-caption: :warning:

:toc-placement!:
endif::[]


= Gradle TestSets plugin

A plugin for the Gradle build system that allows specifying test sets (like integration or acceptance tests).
A test set is a logical grouping of a source set and related dependency configurations, tasks and artifacts.

The plugin requires Gradle 5.1 or higher.

toc::[]


== Quickstart

One of the most common use cases for this plugin is to separate _integration tests_ from unit tests within the same
project. Using a separate test set (instead of other mechanisms like JUnit tags) allows for a clean separation of the
code, as well as a different set of library dependencies for both types of tests.

Add the following to your build.gradle file:

[source,groovy]
----
// The plugins block needs to be at the top of your build script
plugins {
    id 'org.unbroken-dome.test-sets' version '4.0.0'
}

testSets {
    integrationTest
}
----

Place your integration test code in `src/integrationTest/java`, and the unit tests (like before) in `src/test/java`.

To execute only the integration tests, run the `integrationTest` Gradle task:

----
./gradlew integrationTest
----

You can add dependencies that are only used in integration tests to the `integrationTestImplementation` configuration:

[source,groovy]
----
dependencies {
    // Wiremock will only be available in integration tests, but not in unit tests
    integrationTestImplementation 'com.github.tomakehurst:wiremock:2.19.0'
}
----


== Usage

=== Applying the plugin

To use the TestSets plugin, include the following in your Gradle script:

.build.gradle
[source,groovy]
----
plugins {
    id 'org.unbroken-dome.test-sets' version '4.0.0'
}
----


=== Prerequisites

The TestSets plugin is designed to work in conjunction with the `java` plugin, or other JVM language plugins that
follow a similar structure. It has been tested to work with `groovy`, `scala`, and `org.jetbrains.kotlin.jvm`.

You will need to run Gradle 5.1 or higher with a JDK 8 or higher to use the plugin.


[TIP]
====
If you want to understand in detail what the test-sets plugin does under the hood, it is recommended to revisit the
explanation of the different dependency configurations used by the
https://docs.gradle.org/current/userguide/java_plugin.html[Java Plugin] in the Gradle user manual.
====


== Test Sets DSL

A test set is a logical grouping of the following:

- a http://gradle.org/docs/current/userguide/java_plugin.html#N11F7B[source set];
- a set of associated
  http://gradle.org/docs/current/userguide/dependency_management.html#sub:configurations)[dependency configurations];
- a http://gradle.org/docs/current/userguide/java_plugin.html#sec:java_test[Test] task to run the tests;
- a http://gradle.org/docs/current/userguide/java_plugin.html#N12A7C[Jar] task to package the tests;
- optionally, an http://gradle.org/docs/current/userguide/artifact_management.html[artifact] that can be published.

To create a new test set, declare it inside the `testSets` block in the project's build.gradle file, like this:

[source,groovy]
----
testSets {
    integrationTest
}
----

In this example "integrationTest" is the name of the test set being created. As part of the process, the TestSets
plugin will automatically create the following objects:

* A source set named `integrationTest`;
* A dependency configuration named `integrationTestImplementation`, which extends from "testImplementation";
* A dependency configuration named `integrationTestRuntimeOnly`, which extends from "testRuntimeOnly";
* A Test task named `integrationTest` which will run the tests in the set;
* A Jar task named `integrationTestJar` which will package the tests.

Now you can place your integration test sources in `src/integrationTest/java` and run them with the
`integrationTest` task.

[TIP]
====
The dependency configurations `integrationTestImplementation`, `integrationTestRuntimeOnly` and so on are
actually created by Gradle as companions to the `integrationTest` source set. The test sets plugin will automatically
make each of them extend from the corresponding `test***` configuration.

This means that you can define a dependency in `testImplementation` and have it available in your integration tests
as well:

[source,groovy]
----
testSets { integrationTest }

dependencies {
    // These dependencies will be available in integration tests as well as unit tests
    testImplementation 'org.junit.jupiter:junit-jupiter-api:5.3.1'
    testRuntimeOnly 'org.junit.jupiter:junit-jupiter-engine:5.3.1'

    // Use the integrationTest-specific configurations if you need a dependency only there
    integrationTestImplementation 'com.github.tomakehurst:wiremock:2.19.0'
}
----
====


[TIP]
====
When using multiple test sets, you will have a separate `Test` task for each. The `tasks.withType` idiom
is useful for applying common configuration to all of them:

[source,groovy]
----
testSets { integrationTest }

// Make all tests use JUnit 5
tasks.withType(Test) {
    useJUnitPlatform()
}
----
====


=== Extending other test sets

A test set can extend from other test sets, inheriting all the corresponding dependency configurations.

[source,groovy]
----
testSets {
    fooTest
    barTest { extendsFrom fooTest }
}
----

This will make all the `barTest*` configurations extend from the corresponding `fooTest*` configurations, as if you
had written:

[source,groovy]
----
configurations {
    barTestImplementation.extendsFrom fooTestImplementation
    barTestCompileOnly.extendsFrom fooTestCompileOnly
    barTestRuntimeOnly.extendsFrom fooTestRuntimeOnly
    barTestAnnotationProcessor.extendsFrom fooTestAnnotationProcessor
}
----

It does _not_ mean, however, that the source (classes / resources) of the extended test set will be available to
the extending test set. To accomplish this, you must additionally define a dependency on the source set's output:

[source,groovy]
----
dependencies {
    fooTestImplementation sourceSets.barTest.output
}
----

You can also use _test libraries_ (see below) to enable sharing code between your test sets.




=== Changing the directory name

For a source set named "myTest", the `java` plugin by default assumes the directories `src/myTest/java` and
`src/myTest/resources`. A different directory name can be specified using the `dirName` on the test set, for example:

[source,groovy]
----
testSets {
    myTest { dirName = 'my-test' }
}
----

Which would change the source set's java and resources directories to `src/my-test/java` and `src/my-test/resources`,
respectively. This also works with any plugin (Groovy, Scala or Kotlin) that adds an extension to the `SourceSet` type.


=== Predefined Unit Test Set

The JVM plugins (`java`, `groovy` and so on) automatically define a source set named `test` to hold unit tests,
`testImplementation` and `testRuntimeOnly` configurations to declare its dependencies, and a `test` task to run
the tests.

This can be viewed as a test set that is already present, and in fact is available under the name `unitTest`.
You can reference and even modify the `unitTest` test set, just like you would any other test set. For example, you
could change the directory name for your unit tests to `unit-test` instead of `test`:

[source,groovy]
----
testSets {
    unitTest { dirName = 'unit-test' }
}
----

All new test sets implicitly extend the "unitTest" set.


=== Running Tests as Part of the Build

By default, the tests in a custom test set are not executed when you call `gradle build`. This is by design, because
other types of tests are slower or more expensive to run than unit tests. In CI builds, running such tests is often
modeled as a separate step in the build pipeline.

If you would like the tests of a test set to be run as part of every build, you can add a dependency from Gradle's
`check` task to the test set's `Test` task:

[source,groovy]
----
testSets {
    integrationTest
}

check.dependsOn integrationTest
----


== Test Libraries

Test libraries are special test sets that allow you to more cleanly factor out common support code that is used by
multiple test sets. For example, if you have a test set named `integrationTest`, and created some custom assertion
helpers that you would like to use from both unit and integration tests, you could place them in a test library:

[source,groovy]
----

testSets {
    libraries { testCommon }

    unitTest {
        imports libraries.testCommon
    }

    integrationTest {
        // You can also import libraries by name
        imports 'testCommon'
    }
}

dependencies {
    // A test library's API dependencies will also be available in
    // importing test sets
    testCommonApi 'org.junit.jupiter:junit-jupiter-api:5.3.1'
    testCommonApi 'org.assertj:assertj-core:3.11.1'

    // A test library's implementation is "private", it will be available
    // at runtime but importing test sets cannot use it from their code
    testCommonImplementation 'com.google.guava:guava:27.0-jre'
}
----

In contrast to a standard test set, a test library makes a distinction between API and implementation dependencies,
similar to the https://docs.gradle.org/current/userguide/java_library_plugin.html[Java Library Plugin] in Gradle
(but within the same project).

Note that we use `imports` instead of `extendsFrom` to use a library, which has somewhat different semantics.
`integrationTest.imports(testCommon)` adds the following connections:

* `integrationTestImplementation` will extend from `testCommonApi`
* `integrationTestImplementation` will have a dependency on the output of the `testCommon` source set
* `integrationTestRuntimeOnly` will extend from `testCommonRuntimeClasspath`

Unlike `extendsFrom`, importing a test library will not inherit any compile-only or annotation processor dependencies.


=== Publishing an artifact

Optionally, an artifact containing the classes and resources of a test set or test library can be added to the
project's output.

To activate this, simply set the `createArtifact` property of the test set to `true`:

[source,groovy]
----
testSets {
    integrationTest { createArtifact = true }
}
----

This will add the artifact `<projectName>-integrationTest.jar` to the project's artifacts.

[TIP]
====
Publishing artifacts is especially useful for test libraries, because it means that you can reuse your common
test code not only in the same project, but also in other projects.
====

You can modify the classifier of the JAR file by setting the `classifier` property on the test set. By default, it
is the name of the test set.

The following example publishes the unit tests as an artifact with the classifier `tests`:

[source,groovy]
----
testSets {
    unitTest {
        createArtifact = true
        classifier = 'tests'
    }
}
----


== Kotlin DSL Support

As the plugin itself is written in Kotlin, it should work with the Gradle Kotlin DSL without problems.

To create a test set, use any of the common idioms from the Kotlin DSL:

[source,kotlin]
----
plugins {
    id("org.unbroken-dome.test-sets") version "4.0.0"
}

testSets {

    // use the creating construct
    val fooTest by creating { /* ... */ }

    // or the create() method
    create("barTest") { /* ... */ }

    // use the libraries "container view" to create a library
    val myTestLib by libraries.creating

    // or declare it inside a libraries block
    libraries {
        create("myOtherTestLib")
    }

    // unitTest is already defined, so we need to use getting instead of creating
    val unitTest by getting {

        imports(myTestLib)

        // in contrast to Groovy, myOtherTestLib won't be available as a dynamic property,
        // so we need to import it by name
        imports("myOtherTestLib")
    }
}
----

The plugin also contains some extension functions to allow creating or configuring test sets by simply
putting their name, similar to Groovy (you need to put the names in quotes, however):

[source,kotlin]
----
import org.unbrokendome.gradle.plugins.testsets.dsl.TestLibrary

plugins {
    id("org.unbroken-dome.test-sets") version "4.0.0"
}

testSets {
    val myTestLib by libraries.creating

    "fooTest"()

    "barTest" {
        imports(myTestLib)

        // You can also reference other test sets or test libraries by name
        extendsFrom("fooTest")
    }

    // unitTest is already present, but we can configure it in the same way
    "unitTest" { imports(myTestLib) }
 }
----


== JaCoCo Support

When using this plugin together with the https://docs.gradle.org/current/userguide/jacoco_plugin.html[JaCoCo plugin],
a `JacocoReport` task will automatically be added for each test set.

For example, creating a test set named `integrationTest` will automatically create a `JacocoReport` task named
`jacocoIntegrationTestReport`.


== IDE Support

Neither Eclipse nor IntelliJ IDEA support the notion of multiple test sets per project / module natively, so what the
plugin does is only a "best fit" so you can at least run the tests from your IDE.

=== Eclipse

When importing the Gradle project into Eclipse, the TestSets plugin can automatically add each test set's dependencies
to the classpath. This behavior is disabled by default since version 3.0 of the plugin, in order to not interfere with
the internal classpath container that is created by the Eclipse Gradle integration.
If necessary, you can enable this behavior by setting the following property in your `gradle.properties` file:

[source,properties]
.gradle.properties
----
org.unbroken-dome.test-sets.modifyEclipseClasspath=true
----

SourceSets that are generated for a test set are automatically mapped to source folders in Eclipse,
without any further configuration. The plugin will try to mark each of these source folders as "test code"
(the icon in the package explorer will have a slightly different shading).

=== IntelliJ IDEA

If you're using the test-sets plugin in IDEA, make sure to check the option "Create separate module per source set"
when importing the Gradle project, or afterwards in your Gradle settings. This will allow IDEA to manage the
dependencies independently for each source set.
