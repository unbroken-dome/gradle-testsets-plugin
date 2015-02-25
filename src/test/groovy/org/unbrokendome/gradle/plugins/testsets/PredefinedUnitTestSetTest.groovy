package org.unbrokendome.gradle.plugins.testsets

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder

import spock.lang.Specification


public class PredefinedUnitTestSetTest extends Specification {
	
	Project project;
	
	
	def setup() {
		project = ProjectBuilder.builder().build()
		project.apply plugin: 'test-sets'
	}
	
	
	def "Project has one predefined 'unitTest' test set"() {
		expect:
			project.testSets['unitTest']
	}
	
	
	def "Predefined 'unitTest' test set has source set 'test'"() {
		expect:
			project.testSets['unitTest'].sourceSetName == 'test'
	}
	
	
	def "Predefined 'unitTest' test set has compile configuration 'testCompile'"() {
		expect:
			project.testSets['unitTest'].compileConfigurationName == 'testCompile'
	}
	
	
	def "Predefined 'unitTest' test set has runtime configuration 'testRuntime'"() {
		expect:
			project.testSets['unitTest'].runtimeConfigurationName == 'testRuntime'
	}
	
	
	def "New test set should extend from 'unitTest' test set"() {
		when:
			project.testSets { myTest }
		then:
			project.testSets['myTest'].extendsFrom == [ project.testSets['unitTest'] ].toSet()
	}
}
