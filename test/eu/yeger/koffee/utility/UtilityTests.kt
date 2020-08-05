package eu.yeger.koffee.utility

import org.junit.jupiter.api.Test

class UtilityTests {

    @Test
    fun `verify that hasTwoDecimalPlaces returns expected results`() {
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

        testCases.forEach { (value, expectedResult) ->
            value.hasTwoDecimalPlaces() shouldBe expectedResult
        }
    }
}
