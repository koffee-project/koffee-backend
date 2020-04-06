package eu.yeger.service

import eu.yeger.model.Credentials
import eu.yeger.model.Result
import eu.yeger.model.User

interface UserService {

    suspend fun getAllUsers(): Result<List<User>>

    suspend fun getUserById(id: String): Result<User?>

    suspend fun createUser(user: User): Result<String>

    suspend fun updateUser(user: User): Result<String>

    suspend fun deleteUserById(id: String): Result<String>

    suspend fun login(credentials: Credentials): Result<String>
}
