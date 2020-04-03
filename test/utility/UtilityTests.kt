package eu.yeger.utility

import kotlin.test.assertEquals
import org.junit.Test

class UtilityTests {

    @Test
    fun `test hasTwoDecimalPlaces`() {
        val testCases = mapOf(
            0.00 to true,
            0.01 to true,
            0.10 to true,
            0.10 to true,
            1.0 to true,
            0.001 to false,
            0.0001 to false,
            1.234567890 to false
        )

        testCases.forEach { (value, expected) ->
            assertEquals(expected, value.hasTwoDecimalPlaces())
        }
    }
}
