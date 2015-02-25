package org.unbrokendome.gradle.plugins.testsets.internal.events;

import org.unbrokendome.gradle.plugins.testsets.dsl.TestSet;


public class DirNameChangedEvent {
	
	private final TestSet testSet;
	private final String dirName;
	
	
	public DirNameChangedEvent(TestSet testSet, String dirName) {
		this.testSet = testSet;
		this.dirName = dirName;
	}
	
	
	
	public TestSet getTestSet() {
		return testSet;
	}
	
	
	
	public String getDirName() {
		return dirName;
	}
}
