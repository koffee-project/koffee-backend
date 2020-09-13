package eu.yeger.koffee.utility

import kotlin.test.assertEquals
import org.junit.jupiter.api.Assertions

infix fun Any?.shouldBe(expected: Any?) =
    assertEquals(expected = expected, actual = this)

infix fun <T> Collection<T>.shouldContain(element: T) =
    Assertions.assertTrue(contains(element), "$element is not included in the collection.")
