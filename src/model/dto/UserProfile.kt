package eu.yeger.model.dto

import eu.yeger.model.domain.Transaction
import eu.yeger.model.domain.User

/**
 * DTO class for metadata and balance of [User]s.
 *
 * @property id The id of the [User].
 * @property name The name of the [User].
 * @property balance The balance of the [User].
 *
 * @author Jan Müller
 */
data class UserProfile(
    val id: String,
    val name: String,
    val balance: Double
)

/**
 * Extension method for turning [User]s into [UserProfile]s by extracting metadata and calculating their balances.
 *
 * @author Jan Müller
 */
fun User.asProfile() = UserProfile(
    id = id,
    name = name,
    balance = transactions.sumByDouble(Transaction::value)
)
