package eu.yeger.service

import eu.yeger.model.domain.Transaction
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
        val item = itemRepository.getById(this.itemId)
        return when {
            item == null || item.amount <= 0 -> Result.UnprocessableEntity("Invalid purchase")
            else -> {
                val transaction = Transaction.Purchase(
                    itemId = this.itemId,
                    amount = this.amount,
                    value = this.amount * item.price
                )
                block(transaction)
            }
        }
    }
}
