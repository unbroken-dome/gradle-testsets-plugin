@file:Suppress("HasPlatformType")

plugins {
    kotlin("jvm")
    `java-gradle-plugin`
    id("com.gradle.plugin-publish") version "1.2.1"
    `maven-publish`
}


kotlin {
    jvmToolchain(11)

    compilerOptions {
        freeCompilerArgs.add("-Xjvm-default=all")
    }
}


val integrationTest: SourceSet by sourceSets.creating

val integrationTestImplementation by configurations.getting {
    extendsFrom(configurations["testImplementation"])
}
configurations.named("integrationTestRuntimeOnly") {
    extendsFrom(configurations["testRuntimeOnly"])
}


dependencies {
    compileOnly(kotlin("stdlib-jdk8"))

    testImplementation(platform("org.junit:junit-bom:5.7.1"))

    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    testImplementation(kotlin("gradle-plugin"))
    testImplementation("com.willowtreeapps.assertk:assertk-jvm:0.24")

    integrationTestImplementation(gradleApi())
}


configurations.all {
    resolutionStrategy.eachDependency {
        if (requested.group == "org.jetbrains.kotlin") {
            useVersion(embeddedKotlinVersion)
        }
    }
}


@Suppress("UnstableApiUsage")
gradlePlugin {
    website.set("https://github.com/unbroken-dome/gradle-testsets-plugin")
    vcsUrl.set("https://github.com/unbroken-dome/gradle-testsets-plugin.git")
    testSourceSets(integrationTest)

    plugins.create("testSetsPlugin") {
        id = "org.unbroken-dome.test-sets"
        implementationClass = "org.unbrokendome.gradle.plugins.testsets.TestSetsPlugin"
        displayName = "Gradle TestSets plugin"
        description = "A plugin for the Gradle build system that allows specifying test sets (like integration or " +
                "acceptance tests). Each test set is a logical grouping of a source set, dependency configurations, " +
                "and related tasks and artifacts."
        tags.addAll("testing","testset","test set","integration test")
    }
}


tasks {
    register<Test>("integrationTest") {
        dependsOn("pluginUnderTestMetadata")
        group = JavaBasePlugin.VERIFICATION_GROUP
        description = "Runs the integration tests."
        testClassesDirs = integrationTest.output.classesDirs
        classpath = integrationTest.runtimeClasspath
    }

    withType<Test> {
        outputs.upToDateWhen { false }
        useJUnitPlatform()
        testLogging.showStandardStreams = true
    }
}
