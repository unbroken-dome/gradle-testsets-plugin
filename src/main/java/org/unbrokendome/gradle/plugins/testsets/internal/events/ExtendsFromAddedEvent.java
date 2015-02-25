package org.unbrokendome.gradle.plugins.testsets.internal.events;

import org.unbrokendome.gradle.plugins.testsets.dsl.TestSet;

public class ExtendsFromAddedEvent {

	private final TestSet testSet;
	private final TestSet superTestSet;


	public ExtendsFromAddedEvent(TestSet testSet, TestSet superTestSet) {
		this.testSet = testSet;
		this.superTestSet = superTestSet;
	}


	public TestSet getTestSet() {
		return testSet;
	}


	public TestSet getSuperTestSet() {
		return superTestSet;
	}
}
