package eu.yeger.service

import eu.yeger.model.domain.Transaction
import eu.yeger.model.domain.TransactionList
import eu.yeger.model.domain.asRefund
import eu.yeger.model.dto.Funding
import eu.yeger.model.dto.Purchase
import eu.yeger.model.dto.Result
import eu.yeger.repository.ItemRepository
import eu.yeger.repository.UserRepository
import eu.yeger.utility.hasTwoDecimalPlaces

class DefaultTransactionService(
    private val userRepository: UserRepository,
    private val itemRepository: ItemRepository
) : TransactionService {

    override suspend fun processFunding(userId: String, funding: Funding): Result<String> {
        return when (userRepository.hasUserWithId(id = userId)) {
            false -> Result.Conflict("User with that id does not exist")
            true -> funding.processed { transaction ->
                userRepository.addTransaction(id = userId, transaction = transaction)
                Result.OK("Funding processed successfully")
            }
        }
    }

    override suspend fun processPurchase(userId: String, purchase: Purchase): Result<String> {
        return when (userRepository.hasUserWithId(id = userId)) {
            false -> Result.Conflict("User with that id does not exist")
            true -> purchase.processed { transaction ->
                userRepository.addTransaction(id = userId, transaction = transaction)
                itemRepository.updateAmount(id = transaction.itemId, change = -transaction.amount)
                Result.OK("Purchase processed successfully")
            }
        }
    }

    override suspend fun refundLastPurchase(userId: String): Result<String> {
        val user = userRepository.getById(userId) ?: return Result.Conflict("User with that id does not exist")

        return user.transactions
            .filter { it is Transaction.Purchase || it is Transaction.Refund }
            .maxWith(TransactionComparator)
            .processRefund { refund ->
                userRepository.addTransaction(id = userId, transaction = refund)
                itemRepository.updateAmount(id = refund.itemId, change = +refund.amount)
                Result.OK("Purchase refunded successfully")
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
            false -> Result.UnprocessableEntity("Invalid amount")
        }
    }

    private suspend inline fun Purchase.processed(block: (Transaction.Purchase) -> Result<String>): Result<String> {
        val item = itemRepository.getById(this.itemId) ?: return Result.Conflict("Item with that id does not exist")
        return when {
            this.amount <= 0 -> Result.UnprocessableEntity("Purchase amount must be larger than zero")
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
            this == null -> Result.Conflict("User has no refundable purchase")
            this is Transaction.Refund -> Result.Conflict("Last purchase has already been refunded")
            System.currentTimeMillis() - this.timestamp >= 60_000 -> Result.Conflict("Refund timespan has expired")
            this is Transaction.Purchase -> {
                block(this.asRefund())
            }
            else -> Result.Conflict("Refund not possible")
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
