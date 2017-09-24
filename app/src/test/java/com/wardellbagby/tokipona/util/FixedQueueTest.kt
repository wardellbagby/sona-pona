package com.wardellbagby.tokipona.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * @author Wardell Bagby
 */
class FixedQueueTest {
    private lateinit var queue: FixedQueue<String>

    @Before
    fun setup() {
        queue = FixedQueue(3)
    }

    @Test
    fun offer() {
        assertTrue(queue.size == 0)
        queue.offer("Thing!")
        assertTrue(queue.size == 1)
        queue.offer("Another Thing!")
        assertTrue(queue.size == 2)
        queue.offer("One more Thing!")
        assertTrue(queue.size == 3)
        queue.offer("One last thing!")
        assertTrue(queue.size == 3)
    }

    @Test
    fun poll() {
        assertTrue(queue.size == 0)
        queue.offer("Thing!")
        assertEquals("Thing!", queue.poll())
        assertTrue(queue.size == 0)
        queue.offer("Another Thing!")
        assertEquals("Another Thing!", queue.poll())
        assertTrue(queue.size == 0)
        queue.offer("One more Thing!")
        assertEquals("One more Thing!", queue.poll())
        assertTrue(queue.size == 0)
        queue.offer("One last thing!")
        assertEquals("One last thing!", queue.poll())
        assertTrue(queue.size == 0)
    }

    @Test
    fun peek() {
        assertTrue(queue.size == 0)
        queue.offer("Thing!")
        assertEquals("Thing!", queue.peek())
        assertTrue(queue.size == 1)
        queue.offer("Another Thing!")
        assertEquals("Thing!", queue.peek())
        assertTrue(queue.size == 2)
        queue.offer("One more Thing!")
        assertEquals("Thing!", queue.peek())
        assertTrue(queue.size == 3)
        queue.offer("One last thing!")
        assertEquals("Another Thing!", queue.peek())
        assertTrue(queue.size == 3)
    }
}