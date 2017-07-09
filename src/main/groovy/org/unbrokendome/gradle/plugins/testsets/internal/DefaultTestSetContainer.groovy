package org.unbrokendome.gradle.plugins.testsets.internal

import org.gradle.api.Project
import org.gradle.api.internal.AbstractNamedDomainObjectContainer
import org.gradle.internal.reflect.Instantiator
import org.unbrokendome.gradle.plugins.testsets.dsl.ConfigurableTestSet
import org.unbrokendome.gradle.plugins.testsets.dsl.TestSet
import org.unbrokendome.gradle.plugins.testsets.dsl.TestSetContainer


class DefaultTestSetContainer extends AbstractNamedDomainObjectContainer<TestSet> implements TestSetContainer {

    private final TestSet predefinedUnitTestSet


    DefaultTestSetContainer(Instantiator instantiator, Project project) {
        super(TestSet.class, instantiator)
        this.predefinedUnitTestSet = new PredefinedUnitTestSet(project)
        super.add(predefinedUnitTestSet)
    }


    @Override
    protected TestSet doCreate(String name) {
        new DefaultTestSet(name)
    }


    boolean add(TestSet testSet) {

        boolean added = super.add(testSet)

        if (added) {
            if (testSet instanceof ConfigurableTestSet) {
                ((ConfigurableTestSet) testSet).extendsFrom(predefinedUnitTestSet)
            }
        }

        return added
    }
}
