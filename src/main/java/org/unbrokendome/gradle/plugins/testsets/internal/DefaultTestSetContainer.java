package org.unbrokendome.gradle.plugins.testsets.internal;

import org.gradle.api.internal.AbstractNamedDomainObjectContainer;
import org.gradle.internal.reflect.Instantiator;
import org.unbrokendome.gradle.plugins.testsets.dsl.ConfigurableTestSet;
import org.unbrokendome.gradle.plugins.testsets.dsl.TestSet;
import org.unbrokendome.gradle.plugins.testsets.dsl.TestSetContainer;
import org.unbrokendome.gradle.plugins.testsets.internal.events.TestSetAddedEvent;

import com.google.common.eventbus.EventBus;

public class DefaultTestSetContainer extends AbstractNamedDomainObjectContainer<TestSet> implements TestSetContainer {

	private final TestSet predefinedUnitTestSet = new PredefinedUnitTestSet();
	private final EventBus eventBus;
	
	
	public DefaultTestSetContainer(Instantiator instantiator, EventBus eventBus) {
		super(TestSet.class, instantiator);
		this.eventBus = eventBus;
		super.add(predefinedUnitTestSet);
	}
	

	@Override
	protected TestSet doCreate(String name) {
		return new DefaultTestSet(name, eventBus);
	}
	

	
	@Override
	public boolean add(TestSet testSet) {
		
		boolean added = super.add(testSet);
		
		if (added) {
			
			eventBus.post(new TestSetAddedEvent(testSet));
			
			if (testSet instanceof ConfigurableTestSet) {
				((ConfigurableTestSet) testSet).extendsFrom(predefinedUnitTestSet);
			}
		}
		
		return added;
	}
}
