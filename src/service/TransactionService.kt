package eu.yeger.service

import eu.yeger.model.domain.Item
import eu.yeger.model.domain.Transaction
import eu.yeger.model.domain.TransactionList
import eu.yeger.model.domain.User
import eu.yeger.model.dto.Funding
import eu.yeger.model.dto.Purchase
import eu.yeger.model.dto.Result

/**
 * Service for [Transaction]s.
 *
 * @author Jan Müller
 */
interface TransactionService {

    /**
     * Processes a [Funding] for the [User] with the given id.
     * Must validate that the [User] exists and the [Funding] is valid.
     *
     * @param userId The id of the [User].
     * @param funding The [Funding] to be processed.
     * @return The [Result] of the operation.
     */
    suspend fun processFunding(userId: String, funding: Funding): Result<String>

    /**
     * Processes a [Purchase] for the [User] with the given id.
     * Must validate that the [User] and [Item] exist and the [Purchase] is valid.
     *
     * @param userId The id of the [User].
     * @param purchase The [Purchase] to be processed.
     * @return The [Result] of the operation.
     */
    suspend fun processPurchase(userId: String, purchase: Purchase): Result<String>

    /**
     * Processes a refund for the [User] with the given id.
     * Must validate that the [User] exists and has a refundable [Transaction].
     *
     * @param userId The id of the [User].
     * @return The [Result] of the operation.
     */
    suspend fun refundLastPurchase(userId: String): Result<String>

    /**
     * Returns all [Transaction]s of the [User] with the given id.
     * Must validate that the [User] exists.
     *
     * @param userId The id of the [User].
     * @return The [Result] of the operation.
     */
    suspend fun getTransactionsOfUser(userId: String): Result<TransactionList>
}
