package eu.yeger.utility

/**
 * Checks if a [Double] has no more than two relevant decimal places.
 *
 * @receiver The [Double] value.
 *
 * @author Jan Müller
 */
fun Double.hasTwoDecimalPlaces() =
    isFinite() &&
    (this * 100).toInt().toDouble() == this * 100
