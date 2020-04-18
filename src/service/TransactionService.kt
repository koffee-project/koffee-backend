package eu.yeger.service

import eu.yeger.model.dto.Funding
import eu.yeger.model.dto.Purchase
import eu.yeger.model.dto.Result

interface TransactionService {

    suspend fun processFunding(userId: String, funding: Funding): Result<String>

    suspend fun processPurchase(userId: String, purchase: Purchase): Result<String>
}
