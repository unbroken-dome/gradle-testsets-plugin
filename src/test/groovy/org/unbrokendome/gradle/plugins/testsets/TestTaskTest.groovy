package org.unbrokendome.gradle.plugins.testsets

import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.testfixtures.ProjectBuilder

import spock.lang.Specification


class TestTaskTest extends Specification {
	
	Project project;
	
	
	def setup() {
		project = ProjectBuilder.builder().build()
		project.apply plugin: 'org.unbroken-dome.test-sets'
	}
	
	
	def "New test set should have associated test task"() {
		when:
			project.testSets { myTest }
		then:
			project.tasks['myTest'] instanceof Test
	}
}
