package eu.yeger.koffee.utility

import kotlin.test.assertEquals

infix fun Any?.shouldBe(expected: Any?) = assertEquals(expected = expected, actual = this)
