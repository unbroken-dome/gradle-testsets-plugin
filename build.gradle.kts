import org.jetbrains.kotlin.gradle.plugin.getKotlinPluginVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    `java-gradle-plugin`
    id("com.gradle.plugin-publish") version "0.10.0"
    `maven-publish`
}


repositories {
    jcenter()
}


val integrationTest: SourceSet by sourceSets.creating


configurations {
    "integrationTestImplementation" { extendsFrom(configurations["testImplementation"]) }
    "integrationTestRuntimeOnly" { extendsFrom(configurations["testRuntimeOnly"]) }
}


dependencies {
    compileOnly(kotlin("stdlib-jdk8"))

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.3.1")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.3.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.3.1")

    testImplementation(kotlin("gradle-plugin"))
    testImplementation("com.willowtreeapps.assertk:assertk-jvm:0.20")

    "integrationTestImplementation"(gradleApi())
}


tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.freeCompilerArgs = listOf("-Xjvm-default=enable")
}


configurations.all {
    resolutionStrategy.eachDependency {
        if (requested.group == "org.jetbrains.kotlin") {
            useVersion(getKotlinPluginVersion()!!)
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
