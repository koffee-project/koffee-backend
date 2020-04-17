package eu.yeger.repository

import eu.yeger.model.domain.Transaction
import eu.yeger.model.domain.User

interface UserRepository {

    suspend fun getAll(): List<User>

    suspend fun getById(id: String): User?

    suspend fun hasUserWithId(id: String): Boolean

    suspend fun insert(user: User)

    suspend fun removeById(id: String)

    suspend fun addTransaction(id: String, transaction: Transaction)
}
