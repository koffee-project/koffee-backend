package eu.yeger.repository

import eu.yeger.model.User

interface UserRepository {

    suspend fun getAll(): List<User>

    suspend fun getByName(name: String): User?

    suspend fun insert(user: User)
}
