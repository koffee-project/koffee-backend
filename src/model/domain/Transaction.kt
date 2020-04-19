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
sealed class Transaction(
    val value: Double,
    val timestamp: Long
) {

    class Funding(
        value: Double,
        timestamp: Long = System.currentTimeMillis()
    ) : Transaction(value, timestamp)

    class Purchase(
        value: Double,
        timestamp: Long = System.currentTimeMillis(),
        val itemId: String,
        val amount: Int
    ) : Transaction(value, timestamp)

    class Refund(
        value: Double,
        timestamp: Long = System.currentTimeMillis(),
        val itemId: String,
        val amount: Int
    ) : Transaction(value, timestamp)
}

fun Transaction.Purchase.asRefund() = Transaction.Refund(
    value = -value,
    itemId = itemId,
    amount = amount
)
