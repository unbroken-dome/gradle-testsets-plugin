package org.unbrokendome.gradle.plugins.testsets.internal.events;

import org.unbrokendome.gradle.plugins.testsets.dsl.TestSet;

public class TestSetAddedEvent {

	private final TestSet testSet;


	public TestSetAddedEvent(TestSet testSet) {
		this.testSet = testSet;
	}


	public TestSet getTestSet() {
		return testSet;
	}
}
