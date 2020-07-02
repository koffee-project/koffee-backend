package eu.yeger.model.domain

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

/**
 * Sealed class for all types of [Transaction]s.
 *
 * @property value The value of the [Transaction].
 * @property timestamp The timestamp of the [Transaction].
 *
 * @author Jan Müller
 */
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

    /**
     * Funding [Transaction].
     *
     * @property value The value of the [Transaction].
     * @property timestamp The timestamp of the [Transaction].
     *
     * @author Jan Müller
     */
    data class Funding(
        override val value: Double,
        override val timestamp: Long = System.currentTimeMillis()
    ) : Transaction()

    /**
     * Purchase [Transaction].
     *
     * @property value The value of the [Transaction].
     * @property timestamp The timestamp of the [Transaction].
     * @property itemId The id of the purchased [Item].
     * @property itemName The name of the purchased [Item].
     * @property amount The purchased amount.
     *
     * @author Jan Müller
     */
    data class Purchase(
        override val value: Double,
        override val timestamp: Long = System.currentTimeMillis(),
        val itemId: String,
        val itemName: String,
        val amount: Int
    ) : Transaction()

    /**
     * Refund [Transaction].
     *
     * @property value The value of the [Transaction].
     * @property timestamp The timestamp of the [Transaction].
     * @property itemId The id of the refunded [Item].
     * @property itemName The name of the refunded [Item].
     * @property amount The refunded amount.
     *
     * @author Jan Müller
     */
    data class Refund(
        override val value: Double,
        override val timestamp: Long = System.currentTimeMillis(),
        val itemId: String,
        val itemName: String,
        val amount: Int
    ) : Transaction()
}

/**
 * Extension method for turning [Transaction.Purchase]s into [Transaction.Refund]s.
 *
 * @receiver The source [Transaction.Purchase].
 *
 * @author Jan Müller
 */
fun Transaction.Purchase.asRefund() = Transaction.Refund(
    value = -value,
    itemId = itemId,
    itemName = itemName,
    amount = amount
)

/**
 * Wrapper class necessary for preventing type erasure. Contains a list of [Transaction]s.
 *
 * @property transactions The list of [Transaction]s.
 *
 * @author Jan Müller
 */
class TransactionList(private val transactions: List<Transaction>) : List<Transaction> by transactions
