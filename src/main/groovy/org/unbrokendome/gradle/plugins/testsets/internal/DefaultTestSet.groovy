package org.unbrokendome.gradle.plugins.testsets.internal

import org.unbrokendome.gradle.plugins.testsets.dsl.ConfigurableTestSet
import org.unbrokendome.gradle.plugins.testsets.dsl.TestSet

import java.util.concurrent.CopyOnWriteArrayList
import java.util.function.Consumer

class DefaultTestSet extends AbstractTestSet implements ConfigurableTestSet {

	final String name;
	private final Set<TestSet> extendsFrom = []
    private String dirName
	boolean createArtifact = true
	String classifier
    private final List<Consumer<TestSet>> extendsFromAddedListeners = new CopyOnWriteArrayList<>()
    private final List<Consumer<String>> dirNameChangeListeners = new CopyOnWriteArrayList<>()


	DefaultTestSet(String name) {
		this.name = name
	}

	
	@Override
	String getDirName() {
        dirName ?: name
	}
	
	
	@Override
	void setDirName(String dirName) {
		this.dirName = dirName;
        dirNameChangeListeners.each { it.accept dirName }
	}


	@Override
	ConfigurableTestSet extendsFrom(TestSet... superTestSets) {
		extendsFromInternal Arrays.asList(superTestSets)
	}


	private ConfigurableTestSet extendsFromInternal(Collection<TestSet> superTestSets) {
        for (superTestSet in superTestSets) {
            extendsFrom << superTestSet
            extendsFromAddedListeners.each { it.accept superTestSet }
        }
		this
	}


	@Override
	Set<TestSet> getExtendsFrom() {
		return Collections.unmodifiableSet(this.extendsFrom)
	}


	@Override
	String getClassifier() {
		classifier ?: name
	}


    @Override
    void whenExtendsFromAdded(Consumer<TestSet> action) {
        extendsFromAddedListeners << action
    }


    @Override
    void whenDirNameChanged(Consumer<String> action) {
        dirNameChangeListeners << action
    }
}
