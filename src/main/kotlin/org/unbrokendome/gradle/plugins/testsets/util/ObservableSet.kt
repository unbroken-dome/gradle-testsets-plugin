package org.unbrokendome.gradle.plugins.testsets.util

import java.util.function.Predicate


internal interface CollectionObserver<E : Any> {

    fun elementAdded(element: E)

    fun elementRemoved(element: E)
}


internal fun <E : Any> collectionObserver(elementAdded: (E) -> Unit, elementRemoved: (E) -> Unit) =
        object : CollectionObserver<E> {
            override fun elementAdded(element: E) {
                elementAdded.invoke(element)
            }
            override fun elementRemoved(element: E) {
                elementRemoved.invoke(element)
            }
        }


internal class ObservableSet<E : Any>(
        private val delegate: MutableSet<E>,
        private val observer: CollectionObserver<E>) : MutableSet<E> by delegate {



    private class ObservableIterator<out E : Any>(
            private val delegate: MutableIterator<E>,
            private val observer: CollectionObserver<E>)
        : MutableIterator<E> by delegate {

        private var currentElement: E? = null

        override fun next(): E =
                delegate.next().also { currentElement = it }


        override fun remove() {
            delegate.remove()
            observer.elementRemoved(currentElement!!)
        }
    }


    override fun add(element: E): Boolean =
            delegate.add(element) && observer.elementAdded(element).let { true }


    override fun addAll(elements: Collection<E>): Boolean {
        var anyAdded = false
        for (element in elements) {
            if (add(element)) {
                anyAdded = true
            }
        }
        return anyAdded
    }


    override fun remove(element: E): Boolean =
            delegate.remove(element) && observer.elementRemoved(element).let { true }


    override fun removeAll(elements: Collection<E>): Boolean =
            observableRemove { it.removeAll(elements) }


    override fun removeIf(filter: Predicate<in E>): Boolean =
            observableRemove { it.removeIf(filter) }


    override fun retainAll(elements: Collection<E>): Boolean =
            observableRemove { it.retainAll(elements) }


    private fun observableRemove(operation: (MutableSet<E>) -> Boolean): Boolean {
        val oldElements = delegate.toList()
        return if (operation(delegate)) {
            for (element in oldElements) {
                if (element !in delegate) {
                    observer.elementRemoved(element)
                }
            }
            true
        } else {
            false
        }
    }


    override fun iterator(): MutableIterator<E> =
            ObservableIterator(delegate.iterator(), observer)


    override fun clear() {
        val elements = delegate.toList()
        delegate.clear()
        for (element in elements) {
            observer.elementRemoved(element)
        }
    }
}


internal fun <E : Any> observableSetOf(observer: CollectionObserver<E>) =
        ObservableSet(mutableSetOf(), observer)


internal fun <E : Any> observableSetOf(elementAdded: (E) -> Unit, elementRemoved: (E) -> Unit) =
        observableSetOf(collectionObserver(elementAdded, elementRemoved))
