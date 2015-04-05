package org.unbrokendome.gradle.plugins.testsets.internal;

import org.gradle.api.internal.AbstractNamedDomainObjectContainer;
import org.gradle.internal.reflect.Instantiator;
import org.unbrokendome.gradle.plugins.testsets.dsl.ConfigurableTestSet;
import org.unbrokendome.gradle.plugins.testsets.dsl.TestSet;
import org.unbrokendome.gradle.plugins.testsets.dsl.TestSetContainer;

public class DefaultTestSetContainer extends AbstractNamedDomainObjectContainer<TestSet> implements TestSetContainer {

	private final TestSet predefinedUnitTestSet = new PredefinedUnitTestSet();
	
	
	public DefaultTestSetContainer(Instantiator instantiator) {
		super(TestSet.class, instantiator);
		super.add(predefinedUnitTestSet);
	}
	

	@Override
	protected TestSet doCreate(String name) {
		return new DefaultTestSet(name);
	}

	
	@Override
	public boolean add(TestSet testSet) {
		
		boolean added = super.add(testSet);
		
		if (added) {
			if (testSet instanceof ConfigurableTestSet) {
				((ConfigurableTestSet) testSet).extendsFrom(predefinedUnitTestSet);
			}
		}
		
		return added;
	}
}
