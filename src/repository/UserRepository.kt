package eu.yeger.repository

import eu.yeger.model.User

interface UserRepository {

    suspend fun getAll(): List<User>

    suspend fun getById(id: String): User?

    suspend fun hasUserWithId(id: String): Boolean

    suspend fun insert(user: User)

    suspend fun removeById(id: String)
}
