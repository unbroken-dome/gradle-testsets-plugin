package org.unbrokendome.gradle.plugins.testsets.dsl

import org.unbrokendome.gradle.plugins.testsets.util.capitalize


internal object NamingConventions {

    fun sourceSetName(testSetName: String) =
            testSetName

    fun testTaskName(testSetName: String) =
            testSetName

    fun jarTaskName(testSetName: String) =
            "${testSetName}Jar"

    fun artifactConfigurationName(testSetName: String) =
            testSetName

    fun jacocoReportTaskName(testTaskName: String) =
            "jacoco${testTaskName.capitalize()}Report"
}
