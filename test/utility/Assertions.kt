package eu.yeger.utility

import eu.yeger.model.User
import kotlin.test.assertEquals
import kotlin.test.assertNull

infix fun Any?.shouldBe(expected: Any?) = assertEquals(expected = expected, actual = this)

infix fun User?.shouldBe(expected: User?) {
    when (this) {
        null -> assertNull(expected)
        else -> assertEquals(expected, this.copy(password = expected?.password))
    }
}

infix fun List<User>.shouldBe(expected: List<User>) {
    val expectedSorted = expected.sortedBy(User::id)
    val actualSorted = this.sortedBy(User::id)

    assertEquals(expectedSorted.size, actualSorted.size)

    val actualIterator = actualSorted.iterator()

    expectedSorted.forEach { expectedUser ->
        actualIterator.next() shouldBe expectedUser
    }
}
