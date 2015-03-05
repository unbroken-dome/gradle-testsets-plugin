package org.unbrokendome.gradle.plugins.testsets.internal;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.unbrokendome.gradle.plugins.testsets.dsl.ConfigurableTestSet;
import org.unbrokendome.gradle.plugins.testsets.dsl.TestSet;
import org.unbrokendome.gradle.plugins.testsets.internal.events.DirNameChangedEvent;
import org.unbrokendome.gradle.plugins.testsets.internal.events.ExtendsFromAddedEvent;

import com.google.common.eventbus.EventBus;

public class DefaultTestSet extends AbstractTestSet implements ConfigurableTestSet {

	private final EventBus eventBus;
	
	private final String name;
	private final Set<TestSet> extendsFrom = new HashSet<>();
	private boolean createArtifact = true;
	private String classifier;
	private String dirName;


	public DefaultTestSet(String name, EventBus eventBus) {
		this.name = name;
		this.eventBus = eventBus;
	}


	@Override
	public String getName() {
		return name;
	}


	@Override
	public boolean isCreateArtifact() {
		return createArtifact;
	}
	
	
	@Override
	public String getDirName() {
		return (dirName != null) ? dirName : name;
	}
	
	
	@Override
	public void setDirName(String dirName) {
		this.dirName = dirName;
		eventBus.post(new DirNameChangedEvent(this, dirName));
	}


	@Override
	public void setCreateArtifact(boolean createArtifact) {
		this.createArtifact = createArtifact;
	}


	@Override
	public ConfigurableTestSet extendsFrom(TestSet... superTestSets) {
		return extendsFromInternal(Arrays.asList(superTestSets));
	}


	private ConfigurableTestSet extendsFromInternal(Collection<TestSet> superTestSets) {
		
		for (TestSet superTestSet : superTestSets) {
			extendsFrom.add(superTestSet);
			eventBus.post(new ExtendsFromAddedEvent(this, superTestSet));
		}
		
		return this;
	}


	@Override
	public Set<TestSet> getExtendsFrom() {
		return Collections.unmodifiableSet(this.extendsFrom);
	}


	@Override
	public String getClassifier() {
		return classifier != null ? classifier : getName();
	}


	@Override
	public void setClassifier(String classifier) {
		this.classifier = classifier;
	}
}
