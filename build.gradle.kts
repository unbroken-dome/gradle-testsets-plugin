@file:Suppress("HasPlatformType")

plugins {
    kotlin("jvm")
    `java-gradle-plugin`
    id("com.gradle.plugin-publish") version "0.14.0"
    `maven-publish`
}


repositories {
    mavenCentral()
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

    testImplementation(kotlin("gradle-plugin"))
    testImplementation("com.willowtreeapps.assertk:assertk-jvm:0.24")

    integrationTestImplementation(gradleApi())
}


tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf("-Xjvm-default=enable")
    }
}


configurations.all {
    resolutionStrategy.eachDependency {
        if (requested.group == "org.jetbrains.kotlin") {
            useVersion(embeddedKotlinVersion)
        }
    }
}


gradlePlugin {
    testSourceSets(integrationTest)

    plugins.create("testSetsPlugin") {
        id = "org.unbroken-dome.test-sets"
        implementationClass = "org.unbrokendome.gradle.plugins.testsets.TestSetsPlugin"
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
        useJUnitPlatform()
    }
}


pluginBundle {
    website = extra["pluginBundle.website"].toString()
    vcsUrl = extra["pluginBundle.vcsUrl"].toString()
    description = extra["pluginBundle.description"].toString()
    tags = extra["pluginBundle.tags"].toString().split(',')

    (plugins) {
        "testSetsPlugin" {
            displayName = extra["pluginBundle.displayName"].toString()
        }
    }
}
