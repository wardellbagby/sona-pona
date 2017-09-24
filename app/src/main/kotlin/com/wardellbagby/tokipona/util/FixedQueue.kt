package com.wardellbagby.tokipona.util

import java.util.AbstractQueue
import java.util.LinkedList

/**
 * A Queue that will pop its oldest added item when a new item is offered that would put its size
 * above [maxSize]
 *
 * @author Wardell Bagby
 */
class FixedQueue<T>(private val maxSize: Int) : AbstractQueue<T>() {
    private val values: LinkedList<T> = LinkedList()

    override val size: Int
        get() = values.size

    override fun iterator() = values.iterator()

    override fun poll(): T = values.poll()

    override fun peek(): T = values.peek()

    override fun offer(item: T): Boolean {
        if (size == maxSize) {
            values.pop()
        }
        return values.offer(item)
    }
}