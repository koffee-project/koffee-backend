package eu.yeger.service

import eu.yeger.model.Result
import eu.yeger.model.User

interface UserService {

    suspend fun getAllUsers(): Result<List<User>>

    suspend fun getUserByName(name: String): Result<User?>

    suspend fun createUser(user: User): Result<String>

    suspend fun updateUser(user: User): Result<String>

    suspend fun deleteUserByName(name: String): Result<String>
}
