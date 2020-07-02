package eu.yeger.utility

/**
 * Checks if a double value has no more than two relevant decimal places.
 *
 * @author Jan Müller
 */
fun Double.hasTwoDecimalPlaces() =
    isFinite() &&
    (this * 100).toInt().toDouble() == this * 100
