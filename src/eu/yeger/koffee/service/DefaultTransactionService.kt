package eu.yeger.koffee.service

import eu.yeger.koffee.model.domain.Item
import eu.yeger.koffee.model.domain.Transaction
import eu.yeger.koffee.model.domain.TransactionList
import eu.yeger.koffee.model.domain.User
import eu.yeger.koffee.model.domain.asRefund
import eu.yeger.koffee.model.dto.Funding
import eu.yeger.koffee.model.dto.Purchase
import eu.yeger.koffee.model.dto.Result
import eu.yeger.koffee.model.dto.andThen
import eu.yeger.koffee.model.dto.withResult
import eu.yeger.koffee.repository.ItemRepository
import eu.yeger.koffee.repository.UserRepository
import eu.yeger.koffee.utility.FUNDING_SUCCESSFUL
import eu.yeger.koffee.utility.INVALID_FUNDING_AMOUNT
import eu.yeger.koffee.utility.INVALID_PURCHASE_AMOUNT
import eu.yeger.koffee.utility.LAST_PURCHASE_ALREADY_REFUNDED
import eu.yeger.koffee.utility.NO_REFUNDABLE_PURCHASE
import eu.yeger.koffee.utility.PURCHASE_SUCCESSFUL
import eu.yeger.koffee.utility.REFUND_EXPIRED
import eu.yeger.koffee.utility.REFUND_NOT_POSSIBLE
import eu.yeger.koffee.utility.REFUND_SUCCESSFUL
import eu.yeger.koffee.utility.hasTwoDecimalPlaces
import eu.yeger.koffee.utility.validateItemExists
import eu.yeger.koffee.utility.validateUserExists

/**
 * Default [TransactionService] implementation.
 *
 * @author Jan Müller
 */
class DefaultTransactionService(
    private val userRepository: UserRepository,
    private val itemRepository: ItemRepository
) : TransactionService {

    override suspend fun processFunding(userId: String, funding: Funding): Result<String> {
        return userRepository
            .validateUserExists(userId)
            .andThen { validateFunding(funding) }
            .withResult { transaction ->
                userRepository.addTransaction(id = userId, transaction = transaction)
            }
            .andThen { Result.ok(FUNDING_SUCCESSFUL) }
    }

    override suspend fun processPurchase(userId: String, purchase: Purchase): Result<String> {
        return userRepository
            .validateUserExists(userId)
            .andThen { itemRepository.validateItemExists(purchase.itemId) }
            .andThen { item -> validatePurchase(item, purchase) }
            .withResult { transaction ->
                userRepository.addTransaction(id = userId, transaction = transaction)
                itemRepository.updateAmount(id = transaction.itemId, change = -transaction.amount)
            }
            .andThen { Result.ok(PURCHASE_SUCCESSFUL) }
    }

    override suspend fun refundLastPurchase(userId: String): Result<String> {
        return userRepository
            .validateUserExists(userId)
            .andThen { user -> getRefundableTransaction(user) }
            .withResult { refund ->
                userRepository.addTransaction(id = userId, transaction = refund)
                itemRepository.updateAmount(id = refund.itemId, change = +refund.amount)
            }
            .andThen { Result.ok(REFUND_SUCCESSFUL) }
    }

    override suspend fun getTransactionsOfUser(userId: String): Result<TransactionList> {
        return userRepository
            .validateUserExists(userId)
            .andThen { user -> Result.ok(user.transactions) }
    }

    private fun validateFunding(funding: Funding): Result<Transaction.Funding> {
        return when (funding.amount.hasTwoDecimalPlaces()) {
            true -> Result.ok(Transaction.Funding(value = funding.amount))
            false -> Result.unprocessableEntity(INVALID_FUNDING_AMOUNT)
        }
    }

    private fun validatePurchase(item: Item, purchase: Purchase): Result<Transaction.Purchase> {
        return when {
            purchase.amount <= 0 -> Result.unprocessableEntity(INVALID_PURCHASE_AMOUNT)
            else -> {
                val transaction = Transaction.Purchase(
                    itemId = purchase.itemId,
                    itemName = item.name,
                    amount = purchase.amount,
                    value = -(purchase.amount * item.price)
                )
                Result.ok(transaction)
            }
        }
    }

    private fun getRefundableTransaction(user: User): Result<Transaction.Refund> {
        val transaction = user.transactions
            .filter { it is Transaction.Purchase || it is Transaction.Refund }
            .maxWithOrNull(TransactionComparator)
        return when {
            transaction == null -> Result.conflict(NO_REFUNDABLE_PURCHASE)
            transaction is Transaction.Refund -> Result.conflict(LAST_PURCHASE_ALREADY_REFUNDED)
            System.currentTimeMillis() - transaction.timestamp >= 60_000 -> Result.conflict(REFUND_EXPIRED)
            transaction is Transaction.Purchase -> Result.ok(transaction.asRefund())
            else -> Result.conflict(REFUND_NOT_POSSIBLE)
        }
    }

    private object TransactionComparator : Comparator<Transaction> {

        override fun compare(first: Transaction, second: Transaction): Int {
            return when {
                first.timestamp > second.timestamp -> 1 // First transaction is newer
                first.timestamp == second.timestamp -> when {
                    second is Transaction.Refund -> -1 // Second transaction is more important
                    first is Transaction.Refund -> 1 // First transaction is more important
                    else -> 0 // Equal
                }
                first.timestamp < second.timestamp -> -1 // Second transaction is newer
                else -> 0 // Equal
            }
        }
    }
}
