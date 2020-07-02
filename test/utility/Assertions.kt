package eu.yeger.utility

import kotlin.test.assertEquals

infix fun Any?.shouldBe(expected: Any?) = assertEquals(expected = expected, actual = this)
