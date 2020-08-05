package eu.yeger.koffee.utility

import kotlin.test.assertEquals
import kotlin.test.assertTrue

infix fun Any?.shouldBe(expected: Any?) = assertEquals(expected = expected, actual = this)

infix fun <T> Collection<T>.shouldContain(element: T) =
    assertTrue("$element is not included in the collection.") { contains(element) }
