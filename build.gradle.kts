@file:Suppress("HasPlatformType")

plugins {
    kotlin("jvm")
    `java-gradle-plugin`
    id("com.gradle.plugin-publish") version "1.1.0"
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


gradlePlugin {
    website.set(extra["pluginBundle.website"].toString())
    vcsUrl.set(extra["pluginBundle.vcsUrl"].toString())
    testSourceSets(integrationTest)

    plugins.create("testSetsPlugin") {
        id = "org.unbroken-dome.test-sets"
        implementationClass = "org.unbrokendome.gradle.plugins.testsets.TestSetsPlugin"
        displayName = extra["pluginBundle.displayName"].toString()
        description = extra["pluginBundle.description"].toString()
        tags.set(extra["pluginBundle.tags"].toString().split(','))
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
