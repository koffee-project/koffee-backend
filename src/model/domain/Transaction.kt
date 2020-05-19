package eu.yeger.model.domain

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = Transaction.Funding::class, name = "funding"),
    JsonSubTypes.Type(value = Transaction.Purchase::class, name = "purchase"),
    JsonSubTypes.Type(value = Transaction.Refund::class, name = "refund")
)
sealed class Transaction {

    abstract val value: Double

    abstract val timestamp: Long

    data class Funding(
        override val value: Double,
        override val timestamp: Long = System.currentTimeMillis()
    ) : Transaction()

    data class Purchase(
        override val value: Double,
        override val timestamp: Long = System.currentTimeMillis(),
        val itemId: String,
        val itemName: String,
        val amount: Int
    ) : Transaction()

    data class Refund(
        override val value: Double,
        override val timestamp: Long = System.currentTimeMillis(),
        val itemId: String,
        val itemName: String,
        val amount: Int
    ) : Transaction()
}

fun Transaction.Purchase.asRefund() = Transaction.Refund(
    value = -value,
    itemId = itemId,
    itemName = itemName,
    amount = amount
)

/**
 * Wrapper class necessary for preventing type erasure.
 */
class TransactionList(private val transactions: List<Transaction>) : List<Transaction> by transactions
