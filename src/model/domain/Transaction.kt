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
    JsonSubTypes.Type(value = Transaction.Purchase::class, name = "purchase")
)
sealed class Transaction(
    val value: Double,
    val timestamp: Long
) {

    class Funding(
        value: Double,
        timestamp: Long
    ) : Transaction(value, timestamp)

    class Purchase(
        value: Double,
        timestamp: Long,
        val itemId: String,
        val amount: Int
    ) : Transaction(value, timestamp)
}
