package eu.yeger.model.domain

class Transaction(
    val type: Type,
    val value: Double,
    val timestamp: Long
) {
    sealed class Type {
        data class Purchase(val itemId: String, val amount: Int) : Type()
        object Funding : Type()
    }
}
