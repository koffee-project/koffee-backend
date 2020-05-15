package eu.yeger.service

import eu.yeger.model.domain.Transaction
import eu.yeger.model.domain.TransactionList
import eu.yeger.model.domain.asRefund
import eu.yeger.model.dto.Funding
import eu.yeger.model.dto.Purchase
import eu.yeger.model.dto.Result
import eu.yeger.repository.ItemRepository
import eu.yeger.repository.UserRepository
import eu.yeger.utility.FUNDING_SUCCESSFUL
import eu.yeger.utility.INVALID_FUNDING_AMOUNT
import eu.yeger.utility.INVALID_PURCHASE_AMOUNT
import eu.yeger.utility.LAST_PURCHASE_ALREADY_REFUNDED
import eu.yeger.utility.NO_ITEM_WITH_THAT_ID
import eu.yeger.utility.NO_REFUNDABLE_PURCHASE
import eu.yeger.utility.NO_USER_WITH_THAT_ID
import eu.yeger.utility.PURCHASE_SUCCESSFUL
import eu.yeger.utility.REFUND_EXPIRED
import eu.yeger.utility.REFUND_NOT_POSSIBLE
import eu.yeger.utility.REFUND_SUCCESSFUL
import eu.yeger.utility.hasTwoDecimalPlaces

class DefaultTransactionService(
    private val userRepository: UserRepository,
    private val itemRepository: ItemRepository
) : TransactionService {

    override suspend fun processFunding(userId: String, funding: Funding): Result<String> {
        return when (userRepository.hasUserWithId(id = userId)) {
            false -> Result.NotFound(NO_USER_WITH_THAT_ID)
            true -> funding.processed { transaction ->
                userRepository.addTransaction(id = userId, transaction = transaction)
                Result.OK(FUNDING_SUCCESSFUL)
            }
        }
    }

    override suspend fun processPurchase(userId: String, purchase: Purchase): Result<String> {
        return when (userRepository.hasUserWithId(id = userId)) {
            false -> Result.NotFound(NO_USER_WITH_THAT_ID)
            true -> purchase.processed { transaction ->
                userRepository.addTransaction(id = userId, transaction = transaction)
                itemRepository.updateAmount(id = transaction.itemId, change = -transaction.amount)
                Result.OK(PURCHASE_SUCCESSFUL)
            }
        }
    }

    override suspend fun refundLastPurchase(userId: String): Result<String> {
        val user = userRepository.getById(userId) ?: return Result.NotFound(NO_USER_WITH_THAT_ID)

        return user.transactions
            .filter { it is Transaction.Purchase || it is Transaction.Refund }
            .maxWith(TransactionComparator)
            .processRefund { refund ->
                userRepository.addTransaction(id = userId, transaction = refund)
                itemRepository.updateAmount(id = refund.itemId, change = +refund.amount)
                Result.OK(REFUND_SUCCESSFUL)
            }
    }

    override suspend fun getTransactionsOfUser(userId: String): Result<TransactionList?> {
        return when (val user = userRepository.getById(id = userId)) {
            null -> Result.NotFound(null)
            else -> Result.OK(user.transactions)
        }
    }

    private inline fun Funding.processed(block: (Transaction.Funding) -> Result<String>): Result<String> {
        return when (this.amount.hasTwoDecimalPlaces()) {
            true -> {
                val transaction = Transaction.Funding(value = this.amount)
                block(transaction)
            }
            false -> Result.UnprocessableEntity(INVALID_FUNDING_AMOUNT)
        }
    }

    private suspend inline fun Purchase.processed(block: (Transaction.Purchase) -> Result<String>): Result<String> {
        val item = itemRepository.getById(this.itemId) ?: return Result.NotFound(NO_ITEM_WITH_THAT_ID)
        return when {
            this.amount <= 0 -> Result.UnprocessableEntity(INVALID_PURCHASE_AMOUNT)
            else -> {
                val transaction = Transaction.Purchase(
                    itemId = this.itemId,
                    amount = this.amount,
                    value = -(this.amount * item.price)
                )
                block(transaction)
            }
        }
    }

    private inline fun Transaction?.processRefund(block: (Transaction.Refund) -> Result<String>): Result<String> {
        return when {
            this == null -> Result.Conflict(NO_REFUNDABLE_PURCHASE)
            this is Transaction.Refund -> Result.Conflict(LAST_PURCHASE_ALREADY_REFUNDED)
            System.currentTimeMillis() - this.timestamp >= 60_000 -> Result.Conflict(REFUND_EXPIRED)
            this is Transaction.Purchase -> {
                block(this.asRefund())
            }
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
