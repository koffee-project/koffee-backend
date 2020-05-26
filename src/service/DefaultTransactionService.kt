package eu.yeger.service

import eu.yeger.model.domain.Item
import eu.yeger.model.domain.Transaction
import eu.yeger.model.domain.TransactionList
import eu.yeger.model.domain.User
import eu.yeger.model.domain.asRefund
import eu.yeger.model.dto.Funding
import eu.yeger.model.dto.Purchase
import eu.yeger.model.dto.Result
import eu.yeger.model.dto.andThen
import eu.yeger.model.dto.withResult
import eu.yeger.repository.ItemRepository
import eu.yeger.repository.UserRepository
import eu.yeger.utility.FUNDING_SUCCESSFUL
import eu.yeger.utility.INVALID_FUNDING_AMOUNT
import eu.yeger.utility.INVALID_PURCHASE_AMOUNT
import eu.yeger.utility.LAST_PURCHASE_ALREADY_REFUNDED
import eu.yeger.utility.NO_REFUNDABLE_PURCHASE
import eu.yeger.utility.NO_USER_WITH_THAT_ID
import eu.yeger.utility.PURCHASE_SUCCESSFUL
import eu.yeger.utility.REFUND_EXPIRED
import eu.yeger.utility.REFUND_NOT_POSSIBLE
import eu.yeger.utility.REFUND_SUCCESSFUL
import eu.yeger.utility.hasTwoDecimalPlaces
import eu.yeger.utility.validateItemExists
import eu.yeger.utility.validateUserExists

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
            .andThen { Result.OK(FUNDING_SUCCESSFUL) }
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
            .andThen { Result.OK(PURCHASE_SUCCESSFUL) }
    }

    override suspend fun refundLastPurchase(userId: String): Result<String> {
        return userRepository
            .validateUserExists(userId)
            .andThen { user -> getRefundableTransaction(user) }
            .withResult { refund ->
                userRepository.addTransaction(id = userId, transaction = refund)
                itemRepository.updateAmount(id = refund.itemId, change = +refund.amount)
            }
            .andThen { Result.OK(REFUND_SUCCESSFUL) }
    }

    override suspend fun getTransactionsOfUser(userId: String): Result<TransactionList> {
        return when (val user = userRepository.getById(id = userId)) {
            null -> Result.NotFound(NO_USER_WITH_THAT_ID)
            else -> Result.OK(user.transactions)
        }
    }

    private fun validateFunding(funding: Funding): Result<Transaction.Funding> {
        return when (funding.amount.hasTwoDecimalPlaces()) {
            true -> Result.OK(Transaction.Funding(value = funding.amount))
            false -> Result.UnprocessableEntity(INVALID_FUNDING_AMOUNT)
        }
    }

    private fun validatePurchase(item: Item, purchase: Purchase): Result<Transaction.Purchase> {
        return when {
            purchase.amount <= 0 -> Result.UnprocessableEntity(INVALID_PURCHASE_AMOUNT)
            else -> {
                val transaction = Transaction.Purchase(
                    itemId = purchase.itemId,
                    itemName = item.name,
                    amount = purchase.amount,
                    value = -(purchase.amount * item.price)
                )
                Result.OK(transaction)
            }
        }
    }

    private fun getRefundableTransaction(user: User): Result<Transaction.Refund> {
        val transaction = user.transactions
            .filter { it is Transaction.Purchase || it is Transaction.Refund }
            .maxWith(TransactionComparator)
        return when {
            transaction == null -> Result.Conflict(NO_REFUNDABLE_PURCHASE)
            transaction is Transaction.Refund -> Result.Conflict(LAST_PURCHASE_ALREADY_REFUNDED)
            System.currentTimeMillis() - transaction.timestamp >= 60_000 -> Result.Conflict(REFUND_EXPIRED)
            transaction is Transaction.Purchase -> Result.OK(transaction.asRefund())
            else -> Result.Conflict(REFUND_NOT_POSSIBLE)
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
