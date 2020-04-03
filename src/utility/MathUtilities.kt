package eu.yeger.utility

fun Double.hasTwoDecimalPlaces() =
    isFinite() &&
    (this * 100).toInt().toDouble() == this * 100
