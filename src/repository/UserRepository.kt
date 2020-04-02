package eu.yeger.repository

import eu.yeger.model.User

interface UserRepository {

    suspend fun getAll(): List<User>

    suspend fun getByName(name: String): User?

    suspend fun hasUserWithName(name: String): Boolean

    suspend fun insert(user: User)

    suspend fun removeByName(name: String)
}
